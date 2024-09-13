import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {ContainerWithDimensions} from "./ContainerWithDimensions";
import React, {useEffect, useState} from "react";

import {Icon, IconButton, SegmentedButtons, useTheme} from "react-native-paper";
import {RemoteIconButton} from "./RemoteIconButton";
import {FlexContainer} from "./FlexContainer";
import {activeQueue, apiSend} from "../utils/Api";
import {View} from "react-native";
import {useAppContent} from "./AppContex";

export function MediaType(props: {
    style?: StyleProp<ViewStyle>,
    width: number,
    height: number,
    room: string
}) {
    const [currentQueue, setCurrentQueue] = useState(undefined as string | undefined);
    const buttonSize = Math.min((props.width - 20) / 6, props.height / 8);
    const appContext = useAppContent();
    const theme = useTheme();

    async function fectchCurrentQueue() {
        setCurrentQueue(await activeQueue(appContext, props.room) || "Tv");
    }

    useEffect(() => {
        fectchCurrentQueue();
    }, []);

    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const buttonStyle: StyleProp<ViewStyle> = {
                height: height / 4,
                justifyContent: "center",
            };

            const iconSize = height / 6;

            return <>
                <View style={{
                    flex: 1,
                    rowGap: 5,
                    flexDirection: "row",
                    justifyContent: "space-around",
                }}>
                    <FlexContainer/>
                    <FlexContainer flex={10}>
                        <RemoteIconButton size={buttonSize - 10}
                                          icon={"home"}
                                          action={"Go_To_Home_In_Media_Center"}
                                          mode={"contained-tonal"}/>
                    </FlexContainer>
                    <FlexContainer/>
                    <FlexContainer flex={10}>
                        <IconButton
                            mode={"contained-tonal"}
                            containerColor={theme.colors.secondaryContainer}
                            icon={"playlist-play"}
                            size={buttonSize - 10}
                            onPress={() => {
                                appContext.setKeyboardView(currentQueue);
                            }}
                        />
                    </FlexContainer>
                    <FlexContainer/>
                    <FlexContainer flex={10}>
                        <IconButton
                            mode={"contained-tonal"}
                            icon={"bullhorn"}
                            containerColor={theme.colors.secondaryContainer}
                            size={buttonSize - 10}
                            onPress={() => {
                                appContext.setKeyboardView("Contents" + currentQueue);
                            }}
                        />
                    </FlexContainer>
                    <FlexContainer/>
                </View>
                <SegmentedButtons
                    value={currentQueue || "Music"}
                    style={{width: "100%", flex: 1}}
                    buttons={[
                        {
                            icon: () => <Icon size={iconSize} source={"music"}/>,
                            value: "Music",
                            style: buttonStyle,
                            disabled: currentQueue === undefined
                        },
                        {
                            icon: () => <Icon size={iconSize} source={"movie-open"}/>,
                            value: "Movie",
                            style: buttonStyle,
                            disabled: currentQueue === undefined
                        },
                        {
                            icon: () => <Icon size={iconSize} source={"television"}/>,
                            value: "Tv",
                            style: buttonStyle,
                            disabled: currentQueue === undefined
                        }
                    ]} onValueChange={(e) => {
                    if (currentQueue === e)
                        switch (e) {
                            case "Music":
                                apiSend(appContext, "Play_Music_In_Media_Center");
                                break;
                            case "Movie":
                                apiSend(appContext, "Play_Movies_In_Media_Center");
                                break;
                            case "Tv":
                                apiSend(appContext, "Play_TV_In_Media_Center");
                                break;
                        }
                    else
                        setCurrentQueue(e);

                }}/>
            </>
        }}
    </ContainerWithDimensions>
}