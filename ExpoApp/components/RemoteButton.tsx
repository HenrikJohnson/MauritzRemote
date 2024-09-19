import React from "react";
import {Button} from "react-native-paper";
import {apiIdle, apiSend} from "../utils/Api";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {IconSource} from "react-native-paper/lib/typescript/components/Icon";
import {useAppContent} from "./AppContex";

const INITIAL_DELAY = 200;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

export function RemoteButton(props: {
    children: React.ReactNode, action: string, size?: number,
    style?: StyleProp<ViewStyle>,
    contentStyle?: StyleProp<ViewStyle>,
    mode?: "elevated" | "outlined" | "contained-tonal" | "contained" | "text",
    color?: string,
    icon?: IconSource
}) {
    const [timeoutId, setTimeoutId] = React.useState(-1);
    const [intervalId, setIntervalId] = React.useState(-1);
    const [active, setActive] = React.useState(false);
    const appContext = useAppContent();

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
                                setIntervalId((oldValue) => {
                                    if (oldValue >= 0)
                                        clearInterval(oldValue);
                                    return res as number;
                                });
                            }
                            return currentActive;
                        })
                    }, SECONDARY_DELAY) as unknown;
                    setTimeoutId((oldValue) => {
                        if (oldValue >= 0)
                            clearTimeout(oldValue);
                        return res as number
                    });
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

    const styles: any = {
        ...(props.style ?? {} as any), display: 'flex', justifyContent: 'center', alignItems: 'center'
    };
    const contentStyle: any = {
        ...(props.contentStyle ?? {} as any)
    };
    if (props.size) {
        contentStyle.width = props.size;
        contentStyle.height = props.size;
    }

    return <Button
        mode={props.mode ?? "elevated"}
        compact={true}
        style={styles}
        contentStyle={contentStyle}
        buttonColor={props.color}
        icon={props.icon}
        onPressIn={(e) => startSending()}
        onPressOut={(e) => stopSending()}
        onPointerLeave={() => stopSending()}
    >
        {props.children}
    </Button>
}