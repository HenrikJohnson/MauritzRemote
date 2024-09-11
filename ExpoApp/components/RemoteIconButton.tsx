import React from "react";
import {IconButton} from "react-native-paper";
import {apiIdle, apiSend} from "../utils/Api";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {IconSource} from "react-native-paper/lib/typescript/components/Icon";
import {useAppContent} from "./AppContex";

const INITIAL_DELAY = 200;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

export function RemoteIconButton(props: {
    icon : IconSource,
    action: string,
    size?: number,
    style?: StyleProp<ViewStyle>,
    iconColor?: string,
    containerColor?: string,
    mode?: 'outlined' | 'contained' | 'contained-tonal',
    color?: string
}) {
    const appContext = useAppContent();
    const [timeoutId, setTimeoutId] = React.useState(-1);
    const [intervalId, setIntervalId] = React.useState(-1);
    const [active, setActive] = React.useState(false);

    function startSending() {
        setTimeout(() => {
            apiSend(appContext, props.action);
            setActive((currentActive) => {
                if (currentActive) {
                    const res = setTimeout(() => {
                        setActive((currentActive) => {
                            if (currentActive) {
                                const res = setInterval(() => {
                                    apiIdle(appContext, props.action);
                                }, REPEAT_INTERVAL) as unknown;
                                setIntervalId(res as number);
                            }
                            return currentActive;
                        })
                    }, SECONDARY_DELAY) as unknown;
                    setTimeoutId(res as number);
                }
                return currentActive;
            })
        }, INITIAL_DELAY);
        setActive(true);
    }

    function stopSending() {
        if (timeoutId >= 0)
            clearTimeout(timeoutId);
        if (intervalId >= 0)
            clearInterval(intervalId);

        setTimeoutId(-1);
        setIntervalId(-1);
        setActive(false);
    }

    return <IconButton
        mode={props.mode}
        icon={props.icon}
        size={props.size}
        style={props.style}
        iconColor={props.iconColor}
        containerColor={props.containerColor}
        onPressIn={(e) => startSending()}
        onPressOut={(e) => stopSending()}
        onPointerLeave={() => stopSending()}
        />
}