import {apiIdle, apiSend} from "./Api";
import {AppContext} from "../components/AppContex";

const INITIAL_DELAY = 100;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

let currentAction: string | undefined;
let currentTimerId = -1;
let currentIntervalId = -1;

function stopSending() {
    if (currentTimerId >= 0)
        clearTimeout(currentTimerId);
    if (currentIntervalId >= 0)
        clearInterval(currentIntervalId);

    currentTimerId = -1;
    currentIntervalId = -1;
    currentAction = undefined;
}

export function startSendingAction(appContext: AppContext, action: string) {
    console.log("startSendingAction", action);
    stopSending();
    currentAction = action;

    setTimeout(() => {
        apiSend(appContext, action);

        if (currentAction === action && currentTimerId < 0) {
            currentTimerId = setTimeout(() => {
                if (currentAction === action && currentIntervalId < 0) {
                    currentIntervalId = setInterval(() => {
                        if (currentAction === action) {
                            apiIdle(appContext, action);
                        }
                    }, REPEAT_INTERVAL) as unknown as number;
                }
            }, SECONDARY_DELAY) as unknown as number;
        }
    }, INITIAL_DELAY);
}

export function stopSendingAction(appContext: AppContext, action: string) {
    console.log("stopSendingAction", action);
    if (action === currentAction) {
        stopSending();
    }
}