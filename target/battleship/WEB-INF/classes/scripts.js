function listener(event) {
    let fieldID = event.target.id.match("([a-zA-Z]*)")[0]
    let id = event.target.id
    if (fieldID == "mainField") {
        let position = id.match("mainField: (.*)")[1];
        position = position.split("-");
        if (ships.size == 0)
            ships.push([position[0], position[1]])
        else {
            let isExist = false;
            ships.forEach(value => {
                if ((value[0] == position[0] & value[1] == position[1]))
                    isExist = true;
            })
            if (isExist == false) {
                ships.push([position[0], position[1]])
            }
        }
        draw(document.getElementById(id), "SHIP");
    } else {
        let position = id.match("enemyField: (.*)")[1];
        position = position.split("-");
        sendShoot([position[0], position[1]]);
    }
}

let login = document.cookie.match("login=([a-zA-Z]*)")[1];
let gameID = document.cookie.match("gameID=([0-9]*)")[1];
let ships = new Array();
let toast;

//Socket functions
let wsURI = "ws://" + document.location.host + "/" + "socket?login=" + login;
let socket = new WebSocket(wsURI);
socket.onmessage = function (evt) {
    let message = JSON.parse(evt.data);
    let action = message.action;
    let field = message.body.split(";")[0]
    let value = message.body.split(";")[1]
    let position = message.body.split(";")[2]
    switch (action) {
        case "win":
            wait();
            document.getElementById("exit").classList.remove("hide");
            alert("You win!Click 'Exit'");
            break;
        case "loose":
            wait();
            document.getElementById("exit").classList.remove("hide");
            alert("You loose!Click 'Exit'");
            break;
        case "giveUp":
            wait();
            document.getElementById("exit").classList.remove("hide");
            alert("Your enemy surrender,click 'Exit'");
            break;
        case "wait":
            wait();
            break;
        case "youMove":
            youMove();
            break;
        case "journal":
            addJournalRecord(message.body);
            break;
        default:
            let cell = document.getElementById(field + ": " + position)
            draw(cell, value);
            break;
    }

}
socket.onclose = ev => alert(ev.reason);

function sendShip(ship) {
    let message = {
        "gameID": gameID,
        "login": login,
        "action": "setShips",
        "xLine": new Array(),
        "yLine": new Array(),
    }
    ship.forEach(value => {
        message.xLine.push(value[0])
        message.yLine.push(value[1])
    })
    let container = document.getElementById("mainField");
    container.childNodes.forEach(value => {
        value.removeEventListener("click", listener, false)
    })
    sendMessage(message);
}

function sendShoot(position) {
    let message = {
        "gameID": gameID,
        "login": login,
        "action": "shoot",
        "xLine": [position[0]],
        "yLine": [position[1]],
    }
    let json = JSON.stringify(message);
    socket.send(json);
}

function sendMessage(message) {
    if (!socket.readyState) {
        setTimeout(() => sendMessage(message), 1000);
    } else socket.send(JSON.stringify(message));
}
//Game functions
function initGameField(fieldID) {
    for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
            let cell = document.createElement("div");
            let id = fieldID + ": " + i + "-" + j;
            cell.id = id
            cell.classList.add("cell");
            cell.addEventListener("click", listener, false);
            document.getElementById(fieldID).appendChild(cell);
        }
    }
    if (fieldID == "enemyField") {
        toast = document.getElementById("toast");
        toast.classList.add("hide");
        sendMessage({
            "gameID": gameID,
            "login": login,
            "action": "getGameFields",
            "xLine": [0],
            "yLine": [0]
        })
    }
    checkMove()
    document.getElementById("exit").classList.add("hide");
}

function sendShips() {
    document.getElementById("sendShips").remove();
    let ship = new Set();
    for (let i = 0; i < 4; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear()
    for (let i = 4; i < 7; i++)
        ship.add(ships[i])
    sendShip(ship)
    ship.clear()
    for (let i = 7; i < 10; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear()
    for (let i = 10; i < 12; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear()
    for (let i = 12; i < 14; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear();
    for (let i = 14; i < 16; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear()
    for (let i = 14; i < 16; i++)
        ship.add(ships[i])
    sendShip(ship);
    ship.clear();
    ship.add(ships[16]);
    sendShip(ship)
    ship.clear();
    ship.add(ships[17]);
    sendShip(ship)
    ship.clear();
    ship.add(ships[18]);
    sendShip(ship)
    ship.clear();
    ship.add(ships[19]);
    sendShip(ship)
}

function wait() {
    let cellsM = document.getElementById("mainField").childNodes;
    let cellsE = document.getElementById("enemyField").childNodes;
    cellsM.forEach(value => {
        value.removeEventListener("click", listener, false)
    })
    cellsE.forEach(value => {
        value.removeEventListener("click", listener, false)
    })
}

function youMove() {
    toast.classList.remove("hide");
    toast.classList.add("show");
    setTimeout(() => {
        toast.classList.remove("show");
        toast.classList.add("hide");
    },5000)
    let cellsE = document.getElementById("enemyField").children;
    for (let i = 0; i < cellsE.length; i++) {
        if (!cellsE[i].classList.contains("hit") | !cellsE[i].classList.contains("miss")) {
            cellsE[i].addEventListener("click", listener, false)
        }
    }
}

function giveUp() {
    sendMessage({
        "gameID": gameID,
        "login": login,
        "action": "giveUp",
        "xLine": [0],
        "yLine": [0]
    })
}

function exit() {
    window.location.href = document.location.protocol  + "/start"

}

function addJournalRecord(body) {
    let player = body.split(";")[0];
    let record = body.split(";")[1] + ":" + body.split(";")[2];
    let journal = document.getElementById("journal");
    let recordContainer = document.createElement("div");
    let playerContainer = document.createElement("div");
    let valueContainer = document.createElement("div");
    if (journal.children.length > 10) {
        for (let i = 0; i < journal.children.length; i++) {
            let node = journal.children[0];
            node.children[0].parentNode.removeChild(node.children[0]);
            node.children[0].parentNode.removeChild(node.children[0]);
            journal.children[0].parentNode.removeChild(journal.children[0]);
        }
    }
    recordContainer.classList.add("record");
    playerContainer.innerText = player + ": ";
    valueContainer.innerText = record;
    recordContainer.appendChild(playerContainer);
    recordContainer.appendChild(valueContainer);
    journal.appendChild(recordContainer);

}

function checkMove() {
    let journal = document.getElementById("journal");
    if (journal.children[journal.childElementCount - 1] === undefined)
        setTimeout(() => checkMove(),1000)
    else {
        let lastRecord = journal.children[journal.childElementCount - 1]
        let player = lastRecord.children[0].innerText.replace(":","");
        let value = lastRecord.children[1].innerText;
        let action = value.split(":")[0];
        if (login == player & action == "MISS"){
            wait();}
        else if (login != player & action == "HIT"){
            wait();}
        else if (login !==player & action == "KILL"){
            wait()}
        else youMove();
    }
}

//Draw functions
function draw(target, value) {
    switch (value) {
        case "SHIP":
            target.classList.add("ship");
            break;
        case "HIT":
            if (target.classList.contains("ship")) {
                target.classList.remove("ship")
            }
            target.classList.add("hurt");
            break;
        case "MISS":
            target.classList.add("fall");
            break;
        case "KILL":
            target.classList.add("kill");
            if (!target.classList.contains("hurt"))
                target.classList.add("hurt");
            break;
    }
}
