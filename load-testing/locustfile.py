from locust import HttpUser, task, between
import logging
import random


class QuickstartUser(HttpUser):
    wait_time = between(1, 2.5)

    @task(10)
    def redirect(self):
        self.client.get(
            "/r/" + random.choice(self.urls)['alias']
        )

    @task
    def shorten(self):
        self.client.post(
            "/urls/shorten",
            headers={
                'Authorization': self.token
            },
            json={
                "uri": "https://google.com"
            }
        )

    def on_start(self):
        self.signup()
        self.login()
        self.get_urls()

    def signup(self):
        self.client.post("/users/signup", json={
            "email": "drew@ex.com",
            "password": "password1"
        })

    def login(self):
        with self.client.post("/users/signin", json={
            "email": "drew@ex.com",
            "password": "password1"
        }) as response:
            self.token = response.json().get('token')

    def get_urls(self):
        response = self.client.get(
            "/urls",
            headers={
                'Authorization': self.token
            }
        )
        self.urls = response.json().get('urls')
