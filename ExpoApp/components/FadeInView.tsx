import React, {PropsWithChildren, useEffect, useRef} from "react";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {Animated} from "react-native";

type FadeInViewProps = PropsWithChildren<{style: ViewStyle}>;

export function FadeInView(props : FadeInViewProps) {
    const fadeAnim = useRef(new Animated.Value(0)).current; // Initial value for opacity: 0

    useEffect(() => {
        Animated.timing(fadeAnim, {
            toValue: 1,
            duration: 500,
            useNativeDriver: true,
        }).start();
    }, [fadeAnim]);

    return (
        <Animated.View // Special animatable View
            style={{
                ...props.style,
                opacity: fadeAnim, // Bind opacity to animated value
            }}>
            {props.children}
        </Animated.View>
    );
}
