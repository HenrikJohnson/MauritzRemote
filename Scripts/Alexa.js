'use strict';

var https = require('https');
const cardTitle = 'Livingroom';
const username = 'username';
const password = 'password';

var auth = 'Basic ' + new Buffer(username + ':' + password).toString('base64');

// --------------- Helpers that build all of the responses -----------------------

function buildSpeechletResponse(title, output, repromptText, shouldEndSession) {
    return {
        outputSpeech: {
            type: 'PlainText',
            text: output,
        },
        card: {
            type: 'Simple',
            title: title,
            content: output,
        },
        reprompt: {
            outputSpeech: {
                type: 'PlainText',
                text: repromptText,
            },
        },
        shouldEndSession: shouldEndSession,
    };
}

function buildResponse(sessionAttributes, speechletResponse) {
    return {
        version: '1.0',
        sessionAttributes,
        response: speechletResponse,
    };
}


// --------------- Functions that control the skill's behavior -----------------------

function getWelcomeResponse(callback) {
    // If we wanted to initialize the session to have some attributes we could add those here.
    const sessionAttributes = {};
    const speechOutput = 'Welcome to ' + cardTitle + ' skill. Tell me what you want to do like for instance "Watch TV" or "Power Off".';
    // If the user either does not reply to the welcome message or says something that is not
    // understood, they will be prompted again with this text.
    const repromptText = 'What would you like to do?';
    const shouldEndSession = false;

    callback(sessionAttributes,
        buildSpeechletResponse(cardTitle, speechOutput, repromptText, shouldEndSession));
}

function handleSessionEndRequest(callback) {
    const cardTitle = 'Session Ended';
    const speechOutput = 'Thank you for trying the Alexa Skills Kit sample. Have a nice day!';
    // Setting this to true ends the session and exits the skill.
    const shouldEndSession = true;

    callback({}, buildSpeechletResponse(cardTitle, speechOutput, null, shouldEndSession));
}

/**
 * Execute command which is the intent name.
 */
function execute(intentName, session, callback) {
    const sessionAttributes = {};

    let repromptText = '';
    let speechOutput = '';

    
    console.log(intentName);

    var options = {
      host: 'home.henrik.org',
      port: 443,
      path: '/remote/send/'+ cardTitle+'/' + intentName,
      method: 'GET',
      headers: {
        accept: '*/*',
        Authorization: auth
      }
    };

    var req = https.request(options, function(res) {
      console.log(res.statusCode);
      res.on('data', function(d) {
        speechOutput = intentName.replace(/_/g, " ");

        callback(sessionAttributes,
             buildSpeechletResponse(cardTitle, speechOutput, null, true));
      });
    });
    req.end();
    
    req.on('error', function(e) {
        speechOutput = "Something went wrong.";
        repromptText = "Try again.";

        callback(sessionAttributes,
             buildSpeechletResponse(cardTitle, speechOutput, repromptText, false));
    });
}

function findDevice(intent, session, callback) {
    const sessionAttributes = {};

    var intentName = intent.name;
    var device = intent.slots.Device;
    if (device && device.value) {
        var callDevice = device.value.replace(/ /g, "_");
        execute(intentName + "in_" + callDevice, session, callback);
    } else {
        var options = {
          host: 'home.henrik.org',
          port: 443,
          path: '/remote/room/'+ cardTitle,
          method: 'GET',
          headers: {
            accept: '*/*',
            Authorization: auth
          }
        };
    
        let repromptText = '';
        let speechOutput = '';
    
        var req = https.request(options, function(res) {
          console.log(res.statusCode);
          res.on('data', function(d) {
              if (d == "0") {
                  execute(intentName + "in_Media_Center", session, callback);
              } else if (d == "1") {
                  execute(intentName + "in_Cable", session, callback);
              } else if (d == "2") {
                  execute(intentName + "in_TV", session, callback);
              } else if (d == "3") {
                  execute(intentName + "in_Receiver", session, callback);
              } else {
                speechOutput = "Something went wrong wrong determining device. Got weird device: " + d;
                repromptText = "Try again. Got weird device: " + d;
        
                callback(sessionAttributes,
                     buildSpeechletResponse(cardTitle, speechOutput, repromptText, false));
              }
          });
        });
        req.end();
        
        req.on('error', function(e) {
            speechOutput = "Something went wrong determining device.";
            repromptText = "Try again.";
    
            callback(sessionAttributes,
                 buildSpeechletResponse(cardTitle, speechOutput, repromptText, false));
        });
    }
}

// --------------- Events -----------------------

/**
 * Called when the session starts.
 */
function onSessionStarted(sessionStartedRequest, session) {
    console.log(`onSessionStarted requestId=${sessionStartedRequest.requestId}, sessionId=${session.sessionId}`);
}

/**
 * Called when the user launches the skill without specifying what they want.
 */
function onLaunch(launchRequest, session, callback) {
    console.log(`onLaunch requestId=${launchRequest.requestId}, sessionId=${session.sessionId}`);

    // Dispatch to your skill's launch.
    getWelcomeResponse(callback);
}

/**
 * Called when the user specifies an intent for this skill.
 */
function onIntent(intentRequest, session, callback) {
    console.log(`onIntent requestId=${intentRequest.requestId}, sessionId=${session.sessionId}`);

    const intent = intentRequest.intent;
    const intentName = intentRequest.intent.name;

    // Dispatch to your skill's intent handlers
    if (intentName === 'AMAZON.HelpIntent') {
        getWelcomeResponse(callback);
    } else if (intentName === 'AMAZON.StopIntent' || intentName === 'AMAZON.CancelIntent') {
        handleSessionEndRequest(callback);
    } else if (intentName.endsWith("_")) {
        findDevice(intent, session, callback);
    } else {
        execute(intentName, session, callback);
    }
}

/**
 * Called when the user ends the session.
 * Is not called when the skill returns shouldEndSession=true.
 */
function onSessionEnded(sessionEndedRequest, session) {
    console.log(`onSessionEnded requestId=${sessionEndedRequest.requestId}, sessionId=${session.sessionId}`);
    // Add cleanup logic here
}


// --------------- Main handler -----------------------

// Route the incoming request based on type (LaunchRequest, IntentRequest,
// etc.) The JSON body of the request is provided in the event parameter.
exports.handler = (event, context, callback) => {
    try {
        console.log(`event.session.application.applicationId=${event.session.application.applicationId}`);

        if (event.session.application.applicationId !== 'amzn1.ask.skill.5e2b99ba-ff85-441d-941b-7eab35dbc46b') {
             callback('Invalid Application ID');
        }

        if (event.session.new) {
            onSessionStarted({ requestId: event.request.requestId }, event.session);
        }

        if (event.request.type === 'LaunchRequest') {
            onLaunch(event.request,
                event.session,
                (sessionAttributes, speechletResponse) => {
                    callback(null, buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === 'IntentRequest') {
            onIntent(event.request,
                event.session,
                (sessionAttributes, speechletResponse) => {
                    callback(null, buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === 'SessionEndedRequest') {
            onSessionEnded(event.request, event.session);
            callback();
        }
    } catch (err) {
        callback(err);
    }
};
