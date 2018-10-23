import { combineReducers } from 'redux';

import { RootState } from '../../models';
import * as groups from './groups';

const {reducer: groupsReducer} = groups;

export const rootReducer = combineReducers<RootState>({
    groupState: groupsReducer
});