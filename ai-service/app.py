from flask import Flask
from routes.categorise import categorise_bp

app = Flask(_name_)

# Register the route
app.register_blueprint(categorise_bp)

@app.route("/")
def home():
    return {"message": "AI Service Running"}

if _name_ == "_main_":
    app.run(debug=True)