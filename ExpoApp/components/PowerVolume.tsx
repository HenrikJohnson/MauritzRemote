import {useTheme} from "react-native-paper";
import PowerOffIcon from "../assets/icons/power_off.svg";
import {RemoteIconButton} from "./RemoteIconButton";
import {useAppContent} from "./AppContex";

export function PowerVolume(props: { width: number, height: number, includeCompress?: boolean }) {
    const theme = useTheme();
    const appContext = useAppContent();

    const navigationSize = (Math.min(props.width / 2, 2 * props.height / 5) + 20) *
        (appContext.expandedNavigation ? 1.5 : 1.0);

    const buttonSize = Math.min(Math.min((props.height - navigationSize) / 5, props.width / 5), 130);

    return <>
        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 10 + (buttonSize + 10) * 3,
        }}
                          size={buttonSize - 10}
                          action={"Increase_Volume"}
                          mode={"contained-tonal"}
                          icon={"volume-plus"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 10 + (buttonSize + 10) * 2,
        }}
                          size={buttonSize - 10}
                          action={"Decrease_Volume"}
                          mode={"contained-tonal"}
                          icon={"volume-minus"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 10 + buttonSize * 4 / 3 + 10,
        }}
                          size={buttonSize * 2 / 3 - 10}
                          action={"Mute"}
                          icon={"volume-mute"}/>

        {props.includeCompress &&
            <RemoteIconButton style={{
                position: 'absolute',
                right: buttonSize * 2 / 3 + 10,
                bottom: 10 + buttonSize * 4 / 3 + 10,
            }}
                              size={buttonSize * 2 / 3 - 10}
                              action={"DRC"}
                              icon={"arrow-collapse-vertical"}/>
        }

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 10,
        }}
                          color={theme.colors.error}
                          size={buttonSize - 10}
                          mode={"contained"}
                          containerColor={theme.colors.errorContainer}
                          iconColor={theme.colors.error}
                          action={"Power_Off"}
                          icon={({size, color}) => <PowerOffIcon height={size * 0.8} width={size * 0.8}
                                                                 fill={color}/>}/>
    </>;
}

