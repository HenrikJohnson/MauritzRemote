import React from "react";
import {Button} from "react-native-paper";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {IconSource} from "react-native-paper/lib/typescript/components/Icon";
import {useAppContent} from "./AppContex";
import {startSendingAction, stopSendingAction} from "../utils/RepeatingButton";

export function RemoteButton(props: {
    children: React.ReactNode, action: string, size?: number,
    style?: StyleProp<ViewStyle>,
    contentStyle?: StyleProp<ViewStyle>,
    mode?: "elevated" | "outlined" | "contained-tonal" | "contained" | "text",
    color?: string,
    icon?: IconSource
}) {
    const appContext = useAppContent();

    function startSending() {
        startSendingAction(appContext, props.action);
    }

    function stopSending() {
        stopSendingAction(appContext, props.action);
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