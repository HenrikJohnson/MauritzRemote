import React, {useState} from 'react';
import {View, ViewProps} from 'react-native';

// @ts-ignore
interface ContainerWithDimensionsProps extends ViewProps {
    children: ({
                   width,
                   height,
               }: {
        width: number;
        height: number;
    }) => React.ReactNode;
}

export const ContainerWithDimensions: React.FC<
    ContainerWithDimensionsProps
> = ({children, ...props}: ContainerWithDimensionsProps) => {
    const [dimensions, setDimensions] = useState({width: 0, height: 0});

    return (
        <View
            onLayout={event =>
                setDimensions({
                    width: event.nativeEvent.layout.width,
                    height: event.nativeEvent.layout.height
                })
            }
            {...props}>
            {dimensions.height > 0 || dimensions.width > 0 ? children(dimensions) : <></>}
        </View>
    );
};

// ────────────────────────────────────────────────────────────────────────────────
// usage

// <ContainerWithDimensions>
//   {({width, height}) => {
//     console.log('width', width);
//     console.log('height', height);
//     return <View />;
//   }}
// </ContainerWithDimensions>;