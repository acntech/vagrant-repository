import {
    FindFilesActionType,
    Modified,
    Error
} from "..";

export interface File {
    providerId: number;
    fileName: string;
    size: number;
}

export interface FileState {
    loading: boolean;
    files: File[];
    error?: Error;
    modified?: Modified;
}

export interface FindFilesLoadingAction {
    type: FindFilesActionType.LOADING,
    loading: boolean
}

export interface FindFilesSuccessAction {
    type: FindFilesActionType.SUCCESS,
    payload: File[]
}

export interface FindFilesErrorAction {
    type: FindFilesActionType.ERROR,
    error: any
}

export type FindFilesAction = FindFilesLoadingAction | FindFilesSuccessAction | FindFilesErrorAction;

export type FileAction = FindFilesAction;
