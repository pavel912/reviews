from flask import Flask, render_template, request, redirect, make_response
import json
import requests
import requests.auth

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
    return render_template("game_list.html", games=games, page_number=page_number, is_authenticated=is_authenticated(request))

@app.route("/auth/login", methods=["GET"])
def login_page():
    return render_template("login.html")

@app.route("/auth/login", methods=["POST"])
def send_login():
    login_req = {}
    login_req['username'] = request.form["username"]
    login_req['password'] = request.form["password"]

    login_response = requests.post(f'{base_address}/auth/login', json=login_req)


    if 400 <= login_response.status_code < 500:
        # display error
        pass

    if 500 <= login_response.status_code:
        # display error
        pass

    token = login_response.json()['token']
    
    response = make_response(redirect("/"))
    response.set_cookie('jwt_token', token)
    
    return response

@app.route("/auth/logout", methods=["GET"])
def logout():
    # delete jwt from backend?
    response = make_response(redirect("/"))
    response.delete_cookie('jwt_token')
    return response

@app.route("/auth/signup", methods=["GET"])
def signup_page():
    return render_template("register.html")

@app.route("/auth/signup", methods=["POST"])
def signup_send():
    sign_req = {}
    sign_req['username'] = request.form["username"]
    sign_req['password'] = request.form["password"]

    if request.form["password"] != request.form["password_cnf"]:
        # display error
        pass

    login_response = requests.post(f'{base_address}/auth/signup', json=sign_req)


    if 400 <= login_response.status_code < 500:
        # display error
        pass

    if 500 <= login_response.status_code:
        # display error
        pass

    return redirect("/auth/login")

@app.route("/games/<id>", methods=['GET'])
def game_page(id):
    game = requests.get(f"{base_address}/games/{id}").json()
    reviews = game["reviews"]
    return render_template("game_page.html", game=game, reviews=reviews, is_authenticated=is_authenticated(request))

@app.route("/games/create", methods=['GET'])
def create_game_page():
    if not is_authenticated(request):
        # display error
        pass

    return render_template("create_game.html", is_authenticated=is_authenticated(request))

@app.route("/games", methods=['POST'])
def create_game():
    crete_req_json = {}
    crete_req_json["name"] = request.form["name"]
    crete_req_json["description"] = request.form["description"]

    if not is_authenticated(request):
        # display error
        pass

    token = get_token_from_cookies(request)

    resp = requests.post(f"{base_address}/games", json=crete_req_json, headers={"Authorization": "Bearer " + token})
    game_id = resp.json()["id"]

    return redirect(f"/games/{game_id}")

@app.route("/games/<game_id>", methods=['POST'])
def create_review(game_id):
    create_review_request = {}
    create_review_request["score"] = request.form["score"]
    create_review_request["reviewText"] = request.form["review_text"]
    create_review_request["gameId"] = game_id

    if not is_authenticated(request):
        # display error
        pass

    token = get_token_from_cookies(request)

    requests.post(f"{base_address}/reviews",json=create_review_request, headers={"Authorization": "Bearer " + token})

    return redirect(f"/games/{game_id}")

def get_token_from_cookies(request):
    return request.cookies.get('jwt_token')

def is_authenticated(request):
    return request.cookies.get('jwt_token') is not None
