import {
    getAuthenticated,
    getAuthenticationKey,
    getRoom, initializeStorage,
    setAuthenticated,
    setAuthenticationKey
} from "../utils/Storage";
import {useEffect, useState} from "react";
import {View} from "react-native";
import {ActivityIndicator, TextInput, Button, Text, Dialog, Portal, useTheme} from 'react-native-paper';
import {makeApiCall} from "../utils/Api";
import {useAppContent} from "./AppContex";

export function RequireAuth(props:  {
    children: React.ReactNode
}) {
    const [hasAuthed, setHasAuthed] = useState(getAuthenticated());
    const [pending, setPending] = useState(true);
    const [apiKey, setApiKey] = useState(getAuthenticationKey());
    const [error, setError] = useState("");
    const theme = useTheme();
    const appContext = useAppContent();

    function submitAuth() {
        setPending(true);
    }

    async function verifyAuth() {
        await initializeStorage();

        if (apiKey) {
            try {
                const response = await makeApiCall(appContext, "room/" + getRoom(), {
                    auth: apiKey
                });
                if (response.ok) {
                    setAuthenticated(true);
                    setHasAuthed(true)
                    if (apiKey && apiKey !== getAuthenticationKey())
                        setAuthenticationKey(apiKey);
                    return;
                } else if (response.status === 401) {
                    setError("Failed to authenticate");
                    setPending(false);
                    return;
                }
            } catch (e) {
            }
            setError("Network error");
            setTimeout(() => verifyAuth(), 1000);
        } else {
            setPending(false);
        }
    }

    useEffect(() => {
        if (pending && !hasAuthed) {
            verifyAuth();
        }
    }, [pending, hasAuthed]);

    if (hasAuthed) {
        return props.children;
    } else {
        return <View style={{
            backgroundColor: theme.colors.background,
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
        }}>
            {pending ?
                <ActivityIndicator size={"large"}/>
                :
                <>
                    <Text style={{color: theme.colors.error}}>
                        {error ? error : "Please authenticate."}
                    </Text>
                    <Portal>
                        <Dialog visible={!pending} onDismiss={submitAuth}>
                            <Dialog.Title>Provide API key</Dialog.Title>
                            <Dialog.Content>
                                <TextInput
                                label={"API key"} value={apiKey}
                                onChangeText={(text) => setApiKey(text)}/>
                            </Dialog.Content>
                            <Dialog.Actions>
                                <Button onPress={submitAuth}>OK</Button>
                            </Dialog.Actions>
                        </Dialog>
                    </Portal>
                </>
            }
        </View>
    }
}
