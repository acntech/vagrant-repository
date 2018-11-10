import { addLocaleData } from "react-intl";
import * as enLocaleData from 'react-intl/locale-data/en';
import { IntlState } from 'react-intl-redux';
import {
    BoxState,
    GroupState,
    NotificationState,
    ProviderState,
    VersionState,
    RootState
} from '../../models';

export const initialNotificationState: NotificationState = {
    notifications: []
};

export const initialGroupState: GroupState = {
    loading: false,
    groups: []
};

export const initialBoxState: BoxState = {
    loading: false,
    boxes: []
};

export const initialVersionState: VersionState = {
    loading: false,
    versions: []
};

export const initialProviderState: ProviderState = {
    loading: false,
    providers: []
};

const INITIAL_STATE_MESSAGES = {
    mainTitle: 'Whatever'
};

export const intitialIntlState: IntlState = {
    locale: 'en',
    messages: { ...INITIAL_STATE_MESSAGES }
};

export const initializeLocales = () => {
    addLocaleData([...enLocaleData]);
}

export const initialRootState: RootState = {
    intl: intitialIntlState,
    notificationState: initialNotificationState,
    groupState: initialGroupState,
    boxState: initialBoxState,
    versionState: initialVersionState,
    providerState: initialProviderState
};