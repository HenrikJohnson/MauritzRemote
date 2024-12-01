import {useTheme} from "react-native-paper";
import PowerOffIcon from "../assets/icons/power_off.svg";
import {RemoteIconButton} from "./RemoteIconButton";
import {useAppContent} from "./AppContex";
import {useEffect, useState} from "react";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";
import {Dimensions} from "react-native";

export function PowerVolume(props: { includeCompress?: boolean }) {
    const theme = useTheme();
    const appContext = useAppContent();

    const [dimensions, setDimensions] = useState(() => {
        return { width: getScreenWidth(), height: getScreenHeight() }
    });

    useEffect(() => {
        const subscription = Dimensions.addEventListener(
            'change',
            ({window, screen}) => {
                setDimensions({ width: getScreenWidth(), height: getScreenHeight()});
            },
        );
        return () => subscription?.remove();
    }, []);

    const navigationSize = (Math.min(dimensions.width / 2, 2 * dimensions.height / 5 - 50)) * (appContext.expandedNavigation ? 1.5 : 1.0) + 20;

    const buttonSize = Math.min(Math.min((dimensions.height - navigationSize) / 5 - 20, dimensions.width / 5), 130);

    return <>
        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 26 + (buttonSize + 10) * 3,
        }}
                          size={buttonSize - 10}
                          action={"Increase_Volume"}
                          mode={"contained-tonal"}
                          icon={"volume-plus"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 26 + (buttonSize + 10) * 2,
        }}
                          size={buttonSize - 10}
                          action={"Decrease_Volume"}
                          mode={"contained-tonal"}
                          icon={"volume-minus"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 26 + buttonSize * 4 / 3 + 10,
        }}
                          size={buttonSize * 2 / 3 - 10}
                          action={"Mute"}
                          icon={"volume-mute"}/>

        {props.includeCompress &&
            <RemoteIconButton style={{
                position: 'absolute',
                right: buttonSize * 2 / 3 + 10,
                bottom: 26 + buttonSize * 4 / 3 + 10,
            }}
                              size={buttonSize * 2 / 3 - 10}
                              action={"DRC"}
                              icon={"arrow-collapse-vertical"}/>
        }

        <RemoteIconButton style={{
            position: 'absolute',
            right: 0,
            bottom: 26,
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

