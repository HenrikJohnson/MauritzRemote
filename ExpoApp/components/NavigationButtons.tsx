import {View} from "react-native";
import {RemoteButton} from "./RemoteButton";
import {Text, Icon, useTheme, IconButton, Portal} from "react-native-paper";
import {ContainerWithDimensions} from "./ContainerWithDimensions";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import React, {useState} from "react";
import {FlexContainer} from "./FlexContainer";
import {RemoteIconButton} from "./RemoteIconButton";
import {determineTextSizes} from "../utils/Layout";

export function NavigationButtons(props: {
    postFix: string,
    additionalText: string[],
    additionalActions: string[],
    style?: StyleProp<ViewStyle>
}) {
    const theme = useTheme();
    const [expanded, setExpanded] = useState(false);

    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const originalSize = Math.min(width, height);
            const scale = expanded ? 1.5 : 1.0;
            const size = originalSize * scale;
            let {textSize, largeText} = determineTextSizes(size);
            const buttonSize = size / 8;

            const topMargin = (height - originalSize) * scale;
            const leftMargin = (width - originalSize) * scale;

            console.log(topMargin, leftMargin);

            return <>
                <View style={{
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                    rowGap: 5,
                    backgroundColor: theme.colors.background,
                    alignSelf: "flex-start",
                    position: "absolute",
                    height: size,
                    width: size,
                    top: expanded ? 0 : (height - originalSize) / 2,
                    left: originalSize - size
                }}>
                    <View style={
                        {
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                            columnGap: 5,
                            flex: 1
                        }
                    }>
                        <FlexContainer flex={3}>
                            <RemoteButton action={props.additionalActions[0]} contentStyle={{
                                width: size * 3 / 9
                            }}>
                                <Text variant={textSize}>{props.additionalText[0]}</Text>
                            </RemoteButton>
                        </FlexContainer>
                        <FlexContainer flex={2}>
                            <RemoteButton action={"Go_Up_In_" + props.postFix}>
                                <Icon source={"chevron-up"} size={buttonSize}/>
                            </RemoteButton>
                        </FlexContainer>
                        <FlexContainer flex={3}>
                            <RemoteButton action={props.additionalActions[1]} contentStyle={{
                                width: size * 3 / 9
                            }}>
                                <Text variant={textSize}>{props.additionalText[1]}</Text>
                            </RemoteButton>
                        </FlexContainer>
                    </View>
                    <View style={
                        {
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                            columnGap: 5,
                            flex: 3,
                        }
                    }>
                        <FlexContainer flex={4}>
                            <RemoteButton action={"Go_Left_In_" + props.postFix}>
                                <Icon source={"chevron-left"} size={buttonSize}/>
                            </RemoteButton>
                        </FlexContainer>
                        <FlexContainer flex={4}>
                            <ContainerWithDimensions style={{
                                height: "100%",
                                width: "100%",
                                margin: 0,
                                padding: 0,
                                alignItems: "center",
                                justifyContent: "center"
                            }}>
                                {({width, height}) => {
                                    return <RemoteIconButton size={Math.min(width, height)}
                                                             action={"Select_In_" + props.postFix}
                                                             mode={"contained"}
                                                             containerColor={theme.colors.onSecondary}
                                                             icon={() => <Text variant={largeText}>OK</Text>}/>
                                }}
                            </ContainerWithDimensions>
                        </FlexContainer>
                        <FlexContainer flex={4}>
                            <RemoteButton action={"Go_Right_In_" + props.postFix}>
                                <Icon source={"chevron-right"} size={buttonSize}/>
                            </RemoteButton>
                        </FlexContainer>
                    </View>
                    <View style={
                        {
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                            columnGap: 5,
                            flex: 1
                        }
                    }>
                        <FlexContainer flex={3}>
                            <RemoteButton action={props.additionalActions[2]} contentStyle={{
                                width: size * 3 / 9
                            }}>
                                <Text variant={textSize}>{props.additionalText[2]}</Text>
                            </RemoteButton>
                        </FlexContainer>
                        <FlexContainer flex={2}>
                            <RemoteButton action={"Go_Down_In_" + props.postFix}>
                                <Icon source={"chevron-down"} size={buttonSize}/>
                            </RemoteButton>
                        </FlexContainer>
                        <FlexContainer flex={3}>
                            <RemoteButton action={props.additionalActions[3]} contentStyle={{
                                width: size * 3 / 9
                            }}>
                                <Text variant={textSize}>{props.additionalText[3]}</Text>
                            </RemoteButton>
                        </FlexContainer>
                    </View>
                    <IconButton mode={"contained-tonal"} style={{
                        position: "absolute",
                        bottom: -50,
                        left: 0,
                        zIndex: 100
                    }} icon={expanded ? "arrow-top-right" : "arrow-bottom-left"}
                                onPress={() => setExpanded(!expanded)}
                    />
                </View>
            </>
        }}
    </ContainerWithDimensions>
}

