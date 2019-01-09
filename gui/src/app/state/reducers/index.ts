import { combineReducers } from 'redux';

import { RootState } from '../../models';
import * as intl from './intl';
import * as notifications from './notifications';
import * as management from './management';
import * as groups from './groups';
import * as boxes from './boxes';
import * as versions from './versions';
import * as providers from './providers';
import * as files from './files';

const { reducer: intlReducer } = intl;
const { reducer: notificationsReducer } = notifications;
const { reducer: managementReducer } = management;
const { reducer: groupsReducer } = groups;
const { reducer: boxesReducer } = boxes;
const { reducer: versionsReducer } = versions;
const { reducer: providersReducer } = providers;
const { reducer: filesReducer } = files;

export const rootReducer = combineReducers<RootState>({
    intl: intlReducer,
    notificationState: notificationsReducer,
    managementState: managementReducer,
    groupState: groupsReducer,
    boxState: boxesReducer,
    versionState: versionsReducer,
    providerState: providersReducer,
    fileState: filesReducer
});