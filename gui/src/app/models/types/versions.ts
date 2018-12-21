import {
    CreateVersionActionType,
    DeleteVersionActionType,
    GetVersionActionType,
    FindVersionsActionType,
    UpdateVersionActionType,
    Box,
    Error,
    Modified
} from '../';

export interface ModifyVersion {
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
    error?: Error;
    modified?: Modified;
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
    error: any
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
    error: any
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
    error: any
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
    error: any
}

export interface DeleteVersionLoadingAction {
    type: DeleteVersionActionType.LOADING,
    loading: boolean
}

export interface DeleteVersionSuccessAction {
    type: DeleteVersionActionType.SUCCESS,
    versionId: number
}

export interface DeleteVersionErrorAction {
    type: DeleteVersionActionType.ERROR,
    error: any
}

export type GetVersionAction = GetVersionLoadingAction | GetVersionSuccessAction | GetVersionErrorAction;
export type FindVersionsAction = FindVersionsLoadingAction | FindVersionsSuccessAction | FindVersionsErrorAction;
export type CreateVersionAction = CreateVersionLoadingAction | CreateVersionSuccessAction | CreateVersionErrorAction;
export type UpdateVersionAction = UpdateVersionLoadingAction | UpdateVersionSuccessAction | UpdateVersionErrorAction;
export type DeleteVersionAction = DeleteVersionLoadingAction | DeleteVersionSuccessAction | DeleteVersionErrorAction;

export type VersionAction = GetVersionAction | FindVersionsAction | CreateVersionAction | UpdateVersionAction | DeleteVersionAction;