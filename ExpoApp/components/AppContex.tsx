import React, {createContext, useState} from "react";

export interface AppContext {
    keyboardView?: string;
    setKeyboardView: (view?: string) => void;
    notification?: string;
    setNotification: (notification?: string) => void;
    queueState: number,
    setQueueState: (state: number) => void;
}

const appContext = createContext({} as AppContext);

export function useAppContent() : AppContext {
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

    return (
        <appContext.Provider value={{
            keyboardView: keyboardView,
            setKeyboardView: setKeyboardView,
            notification: notification,
            setNotification: setNotification,
            queueState: queueState,
            setQueueState: setQueueState
        }}>
            {props.children}
        </appContext.Provider>
    );
}