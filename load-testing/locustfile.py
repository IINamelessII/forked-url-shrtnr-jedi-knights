import logging
import string
import random

from locust import HttpUser, task, between
RNG_STR_LENGHT = (5, 16)
RNG_PASSWORD_LENGHT = (8, 26)


class QuickstartUser(HttpUser):
    wait_time = between(1, 2.5)

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

    @task(10)
    def redirect(self):
        if not self.store['aliases_all']:
            return

        alias = random.choice(list(self.store['aliases_all']))
        self.client.get(
            f'/r/{alias}',
            name="/r/{alias}"
        )

    @task(3)
    def shorten(self):
        token = self.select_token()
        if not token:
            return
        
        url = get_rnd_url()
        
        json = {
            "uri": url
        }

        if random.random() < .3:
            json['alias'] = rng_str()[:10]

        self.client.post(
            "/urls/shorten",
            headers={
                'Authorization': token,
            },
            json={
                "uri": url
            }
        )

    @task
    def delete(self):
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

    @task(2)
    def signup(self):
        email = f'{rng_str()}@example.com'
        password = rng_passwd()

        response = self.client.post("/users/signup", json={
            "email": email,
            "password": password,
        })

        if response.ok:
            self.store['users'].append({
                'email': email,
                'password': password,
            })

    @task(3)
    def login(self):

        if not self.store['users']:
            return

        user = random.choice(list(self.store['users']))

        response = self.client.post("/users/signin", json={
            'email': user['email'],
            'password': user['password'],
        })

        if response.ok:
            token = response.json()['token']
            self.store['tokens'][user['email']] = token
            self.store['aliases'].setdefault(token, [])

    @task(4)
    def get_urls(self):

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
        if not self.store['tokens']:
            return False

        _email = random.choice(list(self.store['tokens']))
        return self.store['tokens'][_email]


def rng_str():
    return ''.join(
        random.choice(string.ascii_letters)
        for _ in range(random.randint(*RNG_STR_LENGHT))
    )

def rng_passwd():
    return ''.join(
        random.choice(string.ascii_letters + string.digits)
        for _ in range(random.randint(*RNG_PASSWORD_LENGHT))
    )



def get_rnd_url():
    return f'https://jsonplaceholder.typicode.com/photos/{random.randint(1, 5000)}'
