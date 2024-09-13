import {RemoteIconButton} from "./RemoteIconButton";
import {RemoteButton} from "./RemoteButton";
import {Icon, Text, useTheme} from "react-native-paper"

export function AdditionalPlayback(props: { width: number, height: number, postfix: string }) {
    const buttonSize = Math.min(Math.min((props.width - 20) / 6, props.height / 8), 140);

    const theme = useTheme();

    return <>
        <RemoteIconButton style={{
            position: 'absolute',
            left: (buttonSize + 10) * 3,
            bottom: 10,
        }}
                          size={buttonSize - 10}
                          icon={"skip-next"}
                          action={"Go_To_Next_In_" + props.postfix}
                          mode={"contained-tonal"}/>

        <RemoteButton color={theme.colors.secondaryContainer} contentStyle={{
            width: buttonSize,
            height: buttonSize * 2 / 3
        }} style={{
            position: 'absolute',
            left: 10,
            bottom: 10 + buttonSize + 30,
        }}
                      icon={() => <Icon source={"undo"} size={buttonSize / 3}/>}
                      action={"Step_Back_In_" + props.postfix}
                      mode={"contained-tonal"}>
            <Text variant={"labelSmall"}>-30s</Text>
        </RemoteButton>

        <RemoteButton contentStyle={{
            width: buttonSize,
            height: buttonSize * 2 / 3,
            flexDirection: 'row-reverse',
        }} style={{
            position: 'absolute',
            left: 10 + (buttonSize + 10) * 2,
            bottom: 10 + buttonSize + 30,
        }}
                      icon={() => <Icon source={"redo"} size={buttonSize / 3}/>}
                      action={"Step_Forward_In_" + props.postfix}
                      mode={"contained-tonal"}>
            <Text variant={"labelSmall"}>+30s</Text>
        </RemoteButton>
    </>;
}

