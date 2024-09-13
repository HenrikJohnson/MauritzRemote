import React, {createContext, useState} from "react";
import {getExpanded, setExpanded} from "../utils/Storage";

export interface AppContext {
    keyboardView?: string;
    setKeyboardView: (view?: string) => void;
    notification?: string;
    expandedNavigation: boolean;
    setExpandedNavigation: (expanded: boolean) => void;
    setNotification: (notification?: string) => void;
    queueState: number,
    setQueueState: (state: number) => void,
    refreshToken: number,
    updateRefreshToken: () => void
}

const appContext = createContext({} as AppContext);

export function useAppContent(): AppContext {
    return React.useContext(appContext);
}

export function AppContextProvider(props: {
    children: React.ReactNode
}) {
    const [keyboardView, setKeyboardView]
        = useState(undefined as string | undefined);
    const [notification, setNotification]
        = useState(undefined as string | undefined);
    const [queueState, setQueueState] = useState(1);
    const [refreshToken, setRefreshToken] = useState(1);
    const [expandedNavigation, setExpandedNavigation] = useState(() => getExpanded());

    function setExpandedNavigationPersist(val: boolean) {
        setExpanded(val);
        setExpandedNavigation(!expandedNavigation);
    }

    return (
        <appContext.Provider value={{
            keyboardView: keyboardView,
            setKeyboardView: setKeyboardView,
            notification: notification,
            setNotification: setNotification,
            queueState: queueState,
            setQueueState: setQueueState,
            expandedNavigation: expandedNavigation,
            setExpandedNavigation: setExpandedNavigationPersist,
            refreshToken: refreshToken,
            updateRefreshToken: () => setRefreshToken((oldValue) => oldValue + 1)
        }}>
            {props.children}
        </appContext.Provider>
    );
}