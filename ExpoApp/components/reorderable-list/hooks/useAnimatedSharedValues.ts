import {useEffect, useRef} from 'react';
import Animated, {cancelAnimation, makeMutable} from 'react-native-reanimated';

function useAnimatedSharedValues<T>(
    initFunc: (index: number) => T,
    size: number,
    shrink = true,
): Animated.SharedValue<T>[] {
    const ref = useRef<Animated.SharedValue<T>[]>([]);

    if (size !== 0 && ref.current.length === 0) {
        const current = [] as Animated.SharedValue<T>[];
        for (let i = 0; i < size; i++) {
            current[i] = makeMutable(initFunc(i));
        }
        ref.current = current;
    }

    if (size > ref.current.length) {
        const current = [...ref.current];
        for (let i = current.length; i < size; i++) {
            current[i] = makeMutable(initFunc(i));
        }
        ref.current = current;
    }

    useEffect(() => {
        const current = [...ref.current];
        if (shrink && size < current.length) {
            for (let i = size; i < current.length; i++) {
                cancelAnimation(current[i]);
            }

            current.splice(size, current.length - size);
            ref.current = current;
        }
    }, [size, initFunc, shrink]);

    useEffect(() => {
        return () => {
            for (let i = 0; i < ref.current.length; i++) {
                cancelAnimation(ref.current[i]);
            }
        };
    }, []);

    return ref.current;
}

export default useAnimatedSharedValues;
