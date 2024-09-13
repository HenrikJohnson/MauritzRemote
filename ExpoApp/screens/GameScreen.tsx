import {View} from "react-native";
import {NavigationButtons} from "../components/NavigationButtons";
import {ContainerWithDimensions} from "../components/ContainerWithDimensions";

import React from "react";
import {BasicPlayback} from "../components/BasicPlayback";
import {AdditionalPlayback} from "../components/AdditionalPlayback";
import {BottomGameButtons, TopGameButtons} from "../components/ChannelButtons";

export function GameScreen(props: { includeCompress?: boolean }) {
    return <ContainerWithDimensions style={{
        flexDirection: 'column-reverse',
        justifyContent: 'space-between',
        rowGap: 5,
        padding: 10,
        height: "100%",
    }}>
        {({width, height}) => {
            return <>
                <View style={{flex: 1}}/>
                <AdditionalPlayback width={width} height={height} postfix={"Media_Center"}/>
                <BasicPlayback width={width} height={height} postfix={"Media_Center"}/>
                <View style={{
                    flex: 2,
                    flexDirection: 'row',
                    columnGap: 5,
                    width: "100%",
                }}>
                    <BottomGameButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                    <View style={{flex: 1}}/>
                </View>
                <View style={{
                    flex: 2,
                    flexDirection: 'row',
                    columnGap: 5,
                    width: "100%"
                }}>
                    <TopGameButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                    <NavigationButtons style={{
                        height: "100%",
                        flex: 1,
                        width: "100%",
                        justifyContent: "center"
                    }} postFix={"Media_Center"}
                                       additionalText={["Home", "Context", "Info", "Back"]}
                                       additionalActions={[
                                           "Show_Menu_In_Media_Center",
                                           "Show_Context_In_Media_Center",
                                           "Show_Info_In_Media_Center",
                                           "Go_Back_In_Media_Center"]}/>
                </View>
            </>;
        }}
    </ContainerWithDimensions>
}
