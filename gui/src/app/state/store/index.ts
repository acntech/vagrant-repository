import { createStore, applyMiddleware, Middleware } from 'redux';
import logger from 'redux-logger';
import thunk from "redux-thunk";

import { RootState } from '../../models';
import { rootReducer } from '../reducers';
import { initialRootState } from './initial-state';

const middlewares: Middleware[] = [];

middlewares.push(thunk);

if (process.env.NODE_ENV === 'development') {
    middlewares.push(logger);
}

export const store = createStore<RootState>(rootReducer, initialRootState, applyMiddleware(...middlewares));
