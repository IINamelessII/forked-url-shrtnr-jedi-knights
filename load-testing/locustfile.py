"""Load Testing Profile for Locust"""

import logging
import random
import string

from locust import HttpUser, task, constant

from .settings import LOAD_BALANCES

RNG_STR_LENGHT = (5, 16)
RNG_PASSWORD_LENGHT = (8, 26)


class UrlShortenerUser(HttpUser):
    """
    Class, based on HttpUser, for load testing Url Shortener application. 
    Contains data store and set of tasks responsible for specific endpoints 
    (/users/signup, /users/signin, /urls etc).
    """

    wait_time = constant(1)

    store = {
        # all existing alias - for redirect
        'aliases_all': set(),
        # users (dicts with email/pass)
        'users': [],
        # token per email
        'tokens': {},
        # aliases per token
        'aliases': {},
    }

    @task(LOAD_BALANCES['redirect'])
    def redirect(self):
        """
        Task related to /r/{alias} entrypoint. 
        
        Sends GET request to random alias from set of aliases (store['aliases_all'])
        """

        if not self.store['aliases_all']:
            return

        alias = random.choice(list(self.store['aliases_all']))
        self.client.get(
            f'/r/{alias}',
            name="/r/{alias}"
        )

    @task(LOAD_BALANCES['shorten'])
    def shorten(self):
        """
        Task related to /urls/shorten entrypoint.

        Sends POST request with json body { uri: required, alias: optional }.
        """
        token = self.select_token()
        if not token:
            return

        url = get_random_url()

        json = {
            "uri": url
        }

        if random.random() < .3:
            json['alias'] = get_random_string()[:10]

        self.client.post(
            "/urls/shorten",
            headers={
                'Authorization': token,
            },
            json={
                "uri": url
            }
        )

    @task(LOAD_BALANCES['delete'])
    def delete(self):
        """
        Task related to /urts/{alias} entrypoint.

        Sends DELETE reqeust to some random alias of some random user.
        
        Also deletes irrelevant data from user-aliases dict (store['aliases'])
        and from general set of aliases (store['aliases_all'])
        """
        token = self.select_token()
        if not token:
            return

        aliases = self.store['aliases'][token]
        if not aliases:
            return

        alias = random.choice(aliases)

        self.client.delete(
            f'/urls/{alias}',
            name="/urls/{alias}",
            headers={
                'Authorization': token,
            },
        )

        self.store['aliases'][token].remove(alias)
        self.store['aliases_all'].remove(alias)

    @task(LOAD_BALANCES['signup'])
    def signup(self):
        """
        Task related to /users/signup entrypoint.

        Sends POST request with randomly generated user data in json body. 
        If response is successful, saves this data to list of users (store['users'])
        """
        email = f'{get_random_string()}@example.com'
        password = get_random_password()

        response = self.client.post("/users/signup", json={
            "email": email,
            "password": password,
        })

        if not response.ok:
            logging.info(
                f"Sign Up failed for provided credentials: {email}/{password}")
            return

        self.store['users'].append({
            'email': email,
            'password': password,
        })

    @task(LOAD_BALANCES['signin'])
    def signin(self):
        """
        Task related to /users/signin entrypoint.

        Sends POST request with random credentials taken from store['users'].
        If response is successful, saves token in email-token dict (store['tokens'])
        """

        if not self.store['users']:
            return

        user = random.choice(list(self.store['users']))

        response = self.client.post("/users/signin", json={
            'email': user['email'],
            'password': user['password'],
        })

        if not response.ok:
            logging.info(f"Sing In failed for user {user['email']}")
            return

        token = response.json()['token']
        self.store['tokens'][user['email']] = token
        self.store['aliases'].setdefault(token, [])

    @task(LOAD_BALANCES['get_urls'])
    def get_urls(self):
        """
        Task related to /urls entrypoint.

        Sends GET request by specific user to get its url list.
        If response if successful, updates user-aliases dict (store['aliases'])
        and general set of aliases (store['aliases_all'])
        """

        token = self.select_token()
        if not token:
            return

        response = self.client.get(
            "/urls",
            headers={
                'Authorization': token,
            }
        )
        urls = response.json().get('urls')
        if not urls:
            return
        aliases = [item['alias'] for item in urls]
        self.store['aliases'][token] = aliases
        self.store['aliases_all'] |= set(aliases)

    def select_token(self):
        """
        Helper method for selecting random token from store.
        """

        if not self.store['tokens']:
            return False

        _email = random.choice(list(self.store['tokens']))
        return self.store['tokens'][_email]


def get_random_string():
    return ''.join(
        random.choice(string.ascii_letters)
        for _ in range(random.randint(*RNG_STR_LENGHT))
    )


def get_random_password():
    return ''.join(
        random.choice(string.ascii_letters + string.digits)
        for _ in range(random.randint(*RNG_PASSWORD_LENGHT))
    )


def get_random_url():
    return f'https://jsonplaceholder.typicode.com/photos/{random.randint(1, 5000)}'
