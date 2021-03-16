from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 2.5)

    @task
    def hello_world(self):
        self.client.get("/r/palevo")

    def on_start(self):
        reponse = self.client.post(
          "/users/signin", json={"email": "aaa@example.com", "password": "password1"}
        )
        print(reponse)
