import React, {useEffect, useState} from "react";
import {GestureHandlerRootView, RectButton, Swipeable} from "react-native-gesture-handler";
import {makeApiCall, queueContents, QueueItem} from "../utils/Api";
import {Animated, View} from "react-native";
import {ContentItem, ContentItemProps} from "./ContentItem";
import ReorderableList, {ReorderableListRenderItemInfo, ReorderableListReorderEvent} from "./reorderable-list";
import {IconButton, useTheme} from "react-native-paper";
import {getRoom} from "../utils/Storage";
import {useAppContent} from "./AppContex";

export interface DeletableContentItemProps extends ContentItemProps {
    item: QueueItem,
    onDelete: () => void
}

function DeletableContentItem(props: DeletableContentItemProps) {
    const theme = useTheme();

    const [dragging, setDragging] = useState(false)

    function renderRightActions(dragX: any) {

        async function deleteQueueItem() {
            try {
                props.onDelete();
                await makeApiCall(props.appContext, "/queue/" + getRoom() + "/" + props.queue + "/" + props.item.queueId, {
                    method: "DELETE"
                });
            } catch (e) {
            }
        }

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
                    onPress={() => deleteQueueItem()}>
                    <IconButton size={40} icon={"delete"}
                                containerColor={theme.colors.errorContainer} iconColor={theme.colors.error}
                                onPress={() => deleteQueueItem()}
                    />
                </RectButton>
            </Animated.View>
        );
    }

    return <Swipeable renderRightActions={(progress, dragX) => renderRightActions(dragX)}
                      onActivated={() => setDragging(true)} onEnded={() => () => setDragging(false)}>
        <ContentItem item={props.item} queue={props.queue} appContext={props.appContext}
                     onLongPress={props.onLongPress} isDragged={props.isDragged} disabled={dragging}/>
    </Swipeable>
}


export function QueueList(props: {queue: string}) {
    const [data, setData] = useState([] as QueueItem[]);
    const [refreshing, setRefreshing] = useState(false);
    const appContext = useAppContent();

    async function fetchContents() {
        setRefreshing(true);
        setData(await queueContents(appContext, props.queue));
        setRefreshing(false);
    }

    useEffect(() => {
        fetchContents();
    }, [props.queue, appContext.queueState]);


    function renderItem(itemProps: ReorderableListRenderItemInfo<QueueItem>) {
        if (itemProps.index === 0)
            return <ContentItem item={itemProps.item} queue={props.queue} appContext={appContext}/>
        else if (itemProps.isDragged)
            return <ContentItem item={itemProps.item} queue={props.queue} appContext={appContext} isDragged={true}/>
        else
            return <DeletableContentItem item={itemProps.item} queue={props.queue} appContext={appContext}
                                         onLongPress={itemProps.drag}
                                         isDragged={itemProps.isDragged}
                                         onDelete={() => {
                                             const newData = [...data];
                                             newData.splice(itemProps.index, 1);
                                             setData(newData);
                                         }}/>
    }

    async function handleReorder({fromIndex, toIndex}: ReorderableListReorderEvent) {
        if (toIndex > 0 && toIndex != fromIndex) {
            const newData = [...data];
            const item = newData[fromIndex];
            const afterItem = newData[toIndex < fromIndex ? toIndex - 1 : toIndex];

            try {
                setRefreshing(true);
                await makeApiCall(appContext, "queue/" + getRoom() + "/" + props.queue + "/" + item.queueId + "/" + afterItem.queueId, {
                    method: "PUT"
                });
            } catch (e) {
            }
        }
        await fetchContents();
    }

    return (
        <GestureHandlerRootView>
            <View style={{flexDirection: "row", flex: 1}}>
                <ReorderableList
                    data={data}
                    refreshing={refreshing}
                    onReorder={handleReorder}
                    renderItem={renderItem}
                    containerStyle={{width: "100%"}}
                    keyExtractor={(item: QueueItem) => String(item.queueId)}
                    dragScale={1.025}
                />
            </View>
        </GestureHandlerRootView>
    );
}
