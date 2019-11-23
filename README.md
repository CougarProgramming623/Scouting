# Oakton Cougar Robotics Scouting App

## Introduction
Welcome to the Oakton Cougar Robotics Scouting App GitHub page!
This repository is for an Andriod Based app and server that we use to expidite the scouting procsess at competitions.
Scouting at robotics consists of watching teams' performance, and then recording data in order to select the best posible teammates to form an alliance with.
The scouting app fully automates this process, so pen and paper notes are no longer needed. 
After entering robot results, the data is automatically exported onto an excel spreadsheet to make analysis of the results easy.

## Requirements

### Pre-Competition 
1. Tablet/Phone that runs the Andriod OS
2. Computer with [Java](https://adoptopenjdk.net/) and [ADB](https://developer.android.com/studio/releases/platform-tools)
3. Cable to connect Tablet/Phone with computer

### Development
1. [Andriod Studio](https://developer.android.com/studio)

## Usage

### Competion
1. Create a new Microsoft Excel Document. Take note of where it is, and make sure you know how to access it from file explorer. Save the document, and then close excel. 
2. Download the latest [release](https://github.com/CougarProgramming623/scouting-2019/releases/latest).
3. Plug the tablet into the computer using the USB cable. Then in the server, press the button labeled “Pull”. You should see match data appearing in the pulled file section.
4. Repeat Step 3 for each tablet. 
5. Once all the data appears in the pulled files section, press the “Export to Excel” button. Decide on what type of data you want (Selectable at the top of the window) and then click the “Export” button at the bottom of the window.
6. Find the excel file you created in Step 1. Click on it, and then press the “Save” button, located towards the bottom right of the screen. A pop-up saying “Do you want to override” will come up. Click “Yes”. Press “Ok” on the next pop-up.
7. Open up the excel file you saved in Step 1. Make sure all the data is there. If something is missing, delete the file and repeat all the steps over. Come and speak to Troy if there is an error message while running the App. 
8. Close all the tabs created by the server. You should have all of your data now.

### Development
1. Open Andriod Studio
2. Clone code from GitHub
3. Import code into Android Studio

## Coding

### Startup
First take a look at the `AbstractScoutingActivity` class. This is the centerpiece for the entire scouting app.


An Android app + server for FTC&amp;FRC robotics that compiles and exports dynamic data based on matches

