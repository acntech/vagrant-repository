import { combineReducers } from 'redux';

import { RootState } from '../../models';
import * as groups from './groups';
import * as boxes from './boxes';

const {reducer: groupsReducer} = groups;
const {reducer: boxesReducer} = boxes;

export const rootReducer = combineReducers<RootState>({
    groupState: groupsReducer,
    boxState: boxesReducer
});