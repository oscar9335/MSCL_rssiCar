import flask
# import werkzeug
from flask import request, send_file, redirect, url_for
import os
from os import walk
from datetime import datetime
import csv

# from flask_autoindex import AutoIndex


app = flask.Flask(__name__)
# AutoIndex(app, browse_root=os.path.curdir)


file_save_path = "/home/mcs/Yang/store_files/demo"


@app.route('/', methods=['GET', 'POST'])
def home():
    return "Hello"



@app.route("/image_store",methods=['GET', 'POST'])
def image_store():
    print("some on calling")

    if request.files:
        print("calling the server")
        image = request.files["image"]

        # videoname = video.filename

        #store the file in the specific dir
        image.save(os.path.join(file_save_path,image.filename))
        
        print("image saved")
        return "OK, Video upload finished"
    else:
        print("Something went wrong while upload video!!!")
        return "Something went wrong while upload video!!!"


@app.route("/receive_RSSI",methods=['GET', 'POST'])
def receive_RSSI():
    print("RSSI fun")
    beacon1_RSSI = request.form["beacon1"]
    beacon2_RSSI = request.form["beacon2"]
    beacon3_RSSI = request.form["beacon3"]
    beacon4_RSSI = request.form["beacon4"]
    beacon5_RSSI = request.form["beacon5"]
    beacon6_RSSI = request.form["beacon6"]
    user_ID = request.form["user_ID"]

    RSSIS = []
    RSSIS.append(beacon1_RSSI)
    RSSIS.append(beacon2_RSSI)
    RSSIS.append(beacon3_RSSI)
    RSSIS.append(beacon4_RSSI)
    RSSIS.append(beacon5_RSSI)
    RSSIS.append(beacon6_RSSI)
    # RSSIS.append(user_ID)

    # create a specific csv file for corresponding user ID
    store_csv = "store_data" + user_ID + ".csv"

    # data write into the csv file "store_data" with append
    # so, you should remove the previous store_data before a new experiment start
    file = open('store_csv',mode='a', newline='')
    writer = csv.writer(file)   
    writer.writerow(RSSIS)
    file.close()


    print(RSSIS)

    return "received RSSI"

@app.route("/timesynchronize",methods=['GET', 'POST'])
def synchronize():

    date_request = request.form["date_request"]
    # print(date_request)

    if date_request:
    # yyyy_MM_dd_hh_mm_ss_SSS
    # ex: 2022_05_20_17_36_45_485
        date_send = datetime.now().strftime("%Y_%m_%d_%H_%M_%S_%f")[:-3]
        # print(date_send)
        # print(type(date_send))

        return date_send
    return "ERROR"



if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)




