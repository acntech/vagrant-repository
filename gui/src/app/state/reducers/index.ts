import { combineReducers } from 'redux';

import { RootState } from '../../models';
import * as intl from './intl';
import * as groups from './groups';
import * as boxes from './boxes';
import * as versions from './versions';
import * as providers from './providers';

const { reducer: intlReducer } = intl;
const { reducer: groupsReducer } = groups;
const { reducer: boxesReducer } = boxes;
const { reducer: versionsReducer } = versions;
const { reducer: providersReducer } = providers;

export const rootReducer = combineReducers<RootState>({
    intl: intlReducer,
    groupState: groupsReducer,
    boxState: boxesReducer,
    versionState: versionsReducer,
    providerState: providersReducer
});