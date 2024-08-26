import React, {useEffect, useState} from "react";
import {GestureHandlerRootView, RectButton, RefreshControl, Swipeable} from "react-native-gesture-handler";
import {makeApiCall, MediaItem, queueContents, QueueItem} from "../utils/Api";
import {Animated, Pressable} from "react-native";
import {ContentItem} from "./ContentItem";
import ReorderableList, {ReorderableListRenderItemInfo, ReorderableListReorderEvent} from "react-native-reorderable-list";
import {IconButton, useTheme} from "react-native-paper";
import {getRoom} from "../utils/Storage";
import {useAppContent} from "./AppContex";

export function QueueList(props: {queue: string}) {
    const [data, setData] = useState([] as QueueItem[]);
    const theme = useTheme();
    const appContext = useAppContent();

    async function fetchContents() {
        setData([]);
        setData(await queueContents(appContext, props.queue));
    }

    useEffect(() => {
        fetchContents();
    }, [props.queue, appContext.queueState]);


    async function selectItem(item: MediaItem) {
        try {
            await makeApiCall(appContext, `queue/${getRoom()}/${props.queue}/${item.itemId}`, {
                method: "POST"
            });
            appContext.setNotification(`Selected ${item.artist} - ${item.title}`)
            appContext.setQueueState(appContext.queueState + 1);
        } catch (e) {
        }
    }

    function renderItem(itemProps: ReorderableListRenderItemInfo<QueueItem>) {
        function renderRightActions(dragX: any) {
            return (
                <Animated.View
                    style={{
                        justifyContent: 'center',
                        alignItems: 'center', opacity: 0.7
                    }}>
                    <RectButton
                        style={{
                            width: "100%",
                            height: "100%",
                            justifyContent: 'center',
                            alignItems: 'center'
                        }}
                        onPress={() => {
                        }}>
                        <IconButton size={40} icon={"delete"}
                                    containerColor={theme.colors.errorContainer} iconColor={theme.colors.error}
                                    onPress={() => {
                                        try {
                                            makeApiCall(appContext, "/queue/" + getRoom() + "/" + props.queue + "/" + itemProps.item.queueId, {
                                                method: "DELETE"
                                            });
                                            const newData = [...data];
                                            newData.splice(itemProps.index, 1);
                                            setData(newData);
                                        } catch (e) {
                                        }
                                    }}
                        />
                    </RectButton>
                </Animated.View>
            );
        }

        if (itemProps.index === 0)
            return <ContentItem item={itemProps.item} queue={props.queue} appContext={appContext}/>
        else
            return <Swipeable renderRightActions={(progress, dragX) => renderRightActions(dragX)}>
                <ContentItem item={itemProps.item} queue={props.queue} appContext={appContext}
                             onLongPress={itemProps.drag} isDragged={itemProps.isDragged}/>
            </Swipeable>
    }

    function handleReorder({fromIndex, toIndex}: ReorderableListReorderEvent) {
        if (toIndex > 0 && toIndex != fromIndex) {
            const newData = [...data];
            const item = newData[fromIndex].queueId;
            const afterItem = newData[toIndex < fromIndex ? toIndex - 1 :  toIndex].queueId;

            try {
                makeApiCall(appContext, "/queue/" + getRoom() + "/" + props.queue + "/" + item + "/" + afterItem, {
                    method: "PUT"
                });

                newData.splice(toIndex, 0, newData.splice(fromIndex, 1)[0]);
                setData(newData);
            } catch (e) {
            }
        } else {
            fetchContents();
        }
    }

    return (
        <GestureHandlerRootView>
            <ReorderableList
                data={data}
                onReorder={handleReorder}
                renderItem={renderItem}
                keyExtractor={(item: QueueItem) => String(item.queueId)}
                dragScale={1.025}
            />
        </GestureHandlerRootView>
    );
};
