from flask import Flask, render_template, request, redirect
import json
import requests

app = Flask(__name__)

PAGE_SIZE = 10;

with open("config.json") as f:
    config = json.load(f)
    base_address = config['server_path']

@app.route("/")
def main_page():
    page_number = request.args.get('pageNumber')
    if page_number is None:
        page_number = 0
    
    page_number = int(page_number)

    games = requests.get(f"{base_address}/games?pageNumber={page_number}&pageSize={PAGE_SIZE}").json()
    return render_template("game_list.html", games=games, page_number=page_number)

@app.route("/games/<id>", methods=['GET'])
def game_page(id):
    game = requests.get(f"{base_address}/games/{id}").json()
    reviews = game["reviews"]
    return render_template("game_page.html", game=game, reviews=reviews)

@app.route("/games/create", methods=['GET'])
def create_game_page():
    return render_template("create_game.html")

@app.route("/games", methods=['POST'])
def create_game():
    crete_req_json = {}
    crete_req_json["name"] = request.form["name"]
    crete_req_json["description"] = request.form["description"]

    resp = requests.post(f"{base_address}/games", json=crete_req_json)
    game_id = resp.json()["id"]

    return redirect(f"/games/{game_id}")

@app.route("/games/<game_id>", methods=['POST'])
def create_review(game_id):
    create_review_request = {}
    create_review_request["author"] = request.form["name"]
    create_review_request["score"] = request.form["score"]
    create_review_request["reviewText"] = request.form["review_text"]
    create_review_request["gameId"] = game_id

    requests.post(f"{base_address}/reviews",json=create_review_request)

    return redirect(f"/games/{game_id}")