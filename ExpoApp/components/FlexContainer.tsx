import {View} from "react-native";

export function FlexContainer(props: {
    children?: React.ReactNode,
    flex?: number
    alignItems?: "center" | "flex-start" | "flex-end" | "stretch" | "baseline" | undefined
}) {
    return <View style={{
        flex: props.flex ?? 1,
        display: "flex",
        justifyContent: "center",
        alignItems: props.alignItems ?? "center"
    }}>
        {props.children}
    </View>
}
