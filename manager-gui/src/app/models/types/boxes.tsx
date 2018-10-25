import { FindBoxesActionType } from '../constants';
import { Error } from './globals';
import { Group } from '../';

export interface Box {
    id: number;
    name: string;
    description: string;
    group: Group;
}

export interface BoxState {
    loading: boolean;
    boxes: Box[];
    error?: any;
}

export const initialBoxState: BoxState = {
    loading: false,
    boxes: []
};

export interface FindBoxesLoadingAction {
    type: FindBoxesActionType.LOADING,
    loading: boolean
}

export interface FindBoxesSuccessAction {
    type: FindBoxesActionType.SUCCESS,
    payload: Box[]
}

export interface FindBoxesErrorAction {
    type: FindBoxesActionType.ERROR,
    error: Error
}

export type FindBoxesAction = FindBoxesLoadingAction | FindBoxesSuccessAction | FindBoxesErrorAction;

export type BoxAction =  FindBoxesAction;