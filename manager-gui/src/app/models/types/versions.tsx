import {
    CreateVersionActionType,
    GetVersionActionType,
    FindVersionsActionType,
    UpdateVersionActionType
} from '../constants';
import { Error } from './globals';
import { Box } from '../';

export interface CreateVersion {
    name: string;
    description?: string;
}

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

export interface CreateVersionLoadingAction {
    type: CreateVersionActionType.LOADING,
    loading: boolean
}

export interface CreateVersionSuccessAction {
    type: CreateVersionActionType.SUCCESS,
    payload: Version
}

export interface CreateVersionErrorAction {
    type: CreateVersionActionType.ERROR,
    error: Error
}

export interface UpdateVersionLoadingAction {
    type: UpdateVersionActionType.LOADING,
    loading: boolean
}

export interface UpdateVersionSuccessAction {
    type: UpdateVersionActionType.SUCCESS,
    payload: Version
}

export interface UpdateVersionErrorAction {
    type: UpdateVersionActionType.ERROR,
    error: Error
}

export type GetVersionAction = GetVersionLoadingAction | GetVersionSuccessAction | GetVersionErrorAction;
export type FindVersionsAction = FindVersionsLoadingAction | FindVersionsSuccessAction | FindVersionsErrorAction;
export type CreateVersionAction = CreateVersionLoadingAction | CreateVersionSuccessAction | CreateVersionErrorAction;
export type UpdateVersionAction = UpdateVersionLoadingAction | UpdateVersionSuccessAction | UpdateVersionErrorAction;

export type VersionAction = GetVersionAction | FindVersionsAction | CreateVersionAction | UpdateVersionAction;