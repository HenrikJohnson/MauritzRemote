import React from "react";
import {IconButton, useTheme} from "react-native-paper";
import {apiIdle, apiSend} from "../utils/Api";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {IconSource} from "react-native-paper/lib/typescript/components/Icon";
import {useAppContent} from "./AppContex";
import {startSendingAction, stopSendingAction} from "../utils/RepeatingButton";

const INITIAL_DELAY = 200;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

export function RemoteIconButton(props: {
    icon: IconSource,
    action: string,
    size?: number,
    style?: StyleProp<ViewStyle>,
    iconColor?: string,
    containerColor?: string,
    mode?: 'outlined' | 'contained' | 'contained-tonal',
    color?: string
}) {
    const appContext = useAppContent();
    const theme = useTheme();

    function startSending() {
        startSendingAction(appContext, props.action);
    }

    function stopSending() {
        stopSendingAction(appContext, props.action);
    }

    return <IconButton
        mode={props.mode}
        icon={props.icon}
        size={props.size}
        style={props.style}
        iconColor={props.iconColor}
        containerColor={props.containerColor ?? theme.colors.secondaryContainer}
        onPressIn={(e) => startSending()}
        onPressOut={(e) => stopSending()}
        onPointerLeave={() => stopSending()}
    />
}