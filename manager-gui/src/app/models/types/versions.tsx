import { GetVersionActionType, FindVersionsActionType } from '../constants';
import { Error } from './globals';
import { Box } from '../';

export interface Version {
    id: number;
    name: string;
    description: string;
    box: Box;
}

export interface VersionState {
    loading: boolean;
    versions: Version[];
    error?: any;
}

export interface GetVersionLoadingAction {
    type: GetVersionActionType.LOADING,
    loading: boolean
}

export interface GetVersionSuccessAction {
    type: GetVersionActionType.SUCCESS,
    payload: Version
}

export interface GetVersionErrorAction {
    type: GetVersionActionType.ERROR,
    error: Error
}

export interface FindVersionsLoadingAction {
    type: FindVersionsActionType.LOADING,
    loading: boolean
}

export interface FindVersionsSuccessAction {
    type: FindVersionsActionType.SUCCESS,
    payload: Version[]
}

export interface FindVersionsErrorAction {
    type: FindVersionsActionType.ERROR,
    error: Error
}

export type GetVersionAction = GetVersionLoadingAction | GetVersionSuccessAction | GetVersionErrorAction;
export type FindVersionsAction = FindVersionsLoadingAction | FindVersionsSuccessAction | FindVersionsErrorAction;

export type VersionAction = GetVersionAction | FindVersionsAction;