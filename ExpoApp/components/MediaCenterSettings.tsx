import {View} from "react-native";
import {Text} from "react-native-paper";
import {ContainerWithDimensions} from "./ContainerWithDimensions";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {RemoteButton} from "./RemoteButton";
import {determineTextSizes} from "../utils/Layout";

export function MediaCenterSettings(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            let {textSize, largeText} = determineTextSizes(size);

            return <View style={{
                flexDirection: 'row',
                justifyContent: 'space-between',
                columnGap: 10,
                alignSelf: "flex-end",
                width: size
            }}>
                <View style={{
                    flex: 1,
                    flexDirection: 'column',
                    rowGap: 10,
                    width: "50%"
                }}>
                    <Text variant={largeText}>DVD</Text>
                    <RemoteButton action={"Go_To_DVD_Menu_In_Media_Center"} mode={"contained-tonal"}>
                        <Text variant={textSize}>Menu</Text>
                    </RemoteButton>
                    <RemoteButton action={"Play_Next_Audio_In_Media_Center"} mode={"contained-tonal"}>
                        <Text variant={textSize}>Audio</Text>
                    </RemoteButton>
                    <RemoteButton action={"Show_Next_Subtitle_In_Media_Center"} mode={"contained-tonal"}>
                        <Text variant={textSize}>Subtitle</Text>
                    </RemoteButton>
                </View>

                <View style={{
                    flex: 1,
                    flexDirection: 'column',
                    rowGap: 10,
                    width: "50%"
                }}>
                    <Text variant={largeText}>Settings</Text>
                    <RemoteButton action={"Show_Video_Menu_In_Media_Center"} mode={"contained-tonal"}>
                        <Text variant={textSize}>Video</Text>
                    </RemoteButton>
                    <RemoteButton action={"Show_Audio_Menu_In_Media_Center"} mode={"contained-tonal"}>
                        <Text variant={textSize}>Audio</Text>
                    </RemoteButton>
                </View>

            </View>
        }}
    </ContainerWithDimensions>
}