from flask import Flask, render_template, request, redirect
import json
import requests

app = Flask(__name__)

with open("config.json") as f:
    config = json.load(f)
    base_address = config['server_path']

@app.route("/")
def main_page():
    games = requests.get(f"{base_address}/games").json()
    return render_template("game_list.html", games=games)

@app.route("/games/<id>", methods=['GET'])
def game_page(id):
    game = requests.get(f"{base_address}/games/{id}").json()
    reviews = requests.get(f"{base_address}/reviews").json()
    return render_template("game_page.html", game=game, reviews=reviews)

@app.route("/games/<game_id>", methods=['POST'])
def create_review(game_id):
    create_review_request = {}
    create_review_request["author"] = request.form["name"]
    create_review_request["score"] = request.form["score"]
    create_review_request["reviewText"] = request.form["review_text"]
    create_review_request["gameId"] = game_id

    requests.post(f"{base_address}/reviews",json=create_review_request)

    return redirect(f"/games/{game_id}")