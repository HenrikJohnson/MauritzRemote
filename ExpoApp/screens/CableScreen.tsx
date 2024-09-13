import {View} from "react-native";
import {NavigationButtons} from "../components/NavigationButtons";
import {BottomChannelButtons, TopChannelButtons} from "../components/ChannelButtons";
import {ContainerWithDimensions} from "../components/ContainerWithDimensions";
import React from "react";
import {BasicPlayback} from "../components/BasicPlayback";
import {IconButton, useTheme} from "react-native-paper";
import {useAppContent} from "../components/AppContex";

export function CableScreen() {
    const keyboard = useAppContent();
    const theme = useTheme();

    return <ContainerWithDimensions style={{
            flexDirection: 'column-reverse',
            justifyContent: 'space-between',
            rowGap: 5,
            padding: 10,
            height: "100%",
        }}>
            {({width, height}) => {
                const buttonSize = Math.min((width - 20) / 6, height / 8);

                return <>
                    <IconButton
                        mode={"contained-tonal"}
                        icon={"keyboard"}
                        containerColor={theme.colors.secondaryContainer}
                        size={buttonSize - 10}
                        style={{
                            position: 'absolute',
                            left: (buttonSize + 10) * 3,
                            bottom: 10,
                        }}
                        onPress={() => {
                            keyboard.setKeyboardView("roku");
                        }}
                    />

                    <BasicPlayback width={width} height={height} postfix={"Cable"}/>

                    <View style={{flex: 1}}/>

                    <View style={{
                        flex: 2,
                        flexDirection: 'row-reverse',
                        columnGap: 5,
                        width: "100%",
                    }}>
                        <View style={{flex: 1}}/>
                        <BottomChannelButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                    </View>

                    <View style={{
                        flex: 2,
                        flexDirection: 'row',
                        columnGap: 5,
                        width: "100%"
                    }}>
                        <TopChannelButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                        <NavigationButtons style={{
                            height: "100%",
                            flex: 1,
                            width: "100%"
                        }} postFix={"Cable"}
                                           additionalText={["Home", "Search", "Info", "Back"]}
                                           additionalActions={["Go_To_Home_In_Cable", "Search_In_Cable", "Show_Info_In_Cable", "Go_Back_In_Cable"]}/>
                    </View>
                </>;
            }}
        </ContainerWithDimensions>
}
