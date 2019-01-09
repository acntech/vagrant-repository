import { addLocaleData } from "react-intl";
import * as enLocaleData from 'react-intl/locale-data/en';
import { IntlState } from 'react-intl-redux';
import {
    BoxState,
    GroupState,
    FileState,
    NotificationState,
    ManagementState,
    ProviderState,
    VersionState,
    RootState
} from '../../models';

export const initialNotificationState: NotificationState = {
    notifications: []
};

export const initialManagementState: ManagementState = {
    loading: false
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

export const initialFileState: FileState = {
    loading: false,
    files: []
};

const INITIAL_STATE_MESSAGES = {
    mainTitle: 'Whatever'
};

export const initialIntlState: IntlState = {
    locale: 'en',
    messages: { ...INITIAL_STATE_MESSAGES }
};

export const initializeLocales = () => {
    addLocaleData([...enLocaleData]);
};

export const initialRootState: RootState = {
    intl: initialIntlState,
    notificationState: initialNotificationState,
    managementState: initialManagementState,
    groupState: initialGroupState,
    boxState: initialBoxState,
    versionState: initialVersionState,
    providerState: initialProviderState,
    fileState: initialFileState
};