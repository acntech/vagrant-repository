import { combineReducers } from 'redux';

import { RootState } from '../../models';
import * as groups from './groups';
import * as boxes from './boxes';
import * as versions from './versions';

const { reducer: groupsReducer } = groups;
const { reducer: boxesReducer } = boxes;
const { reducer: versionsReducer } = versions;

export const rootReducer = combineReducers<RootState>({
    groupState: groupsReducer,
    boxState: boxesReducer,
    versionState: versionsReducer
});