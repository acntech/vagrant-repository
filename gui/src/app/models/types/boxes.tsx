import {
    CreateBoxActionType,
    GetBoxActionType,
    FindBoxesActionType,
    UpdateBoxActionType,
    Error,
    Group,
    Modified
} from '../';

export interface CreateBox {
    name: string;
    description?: string;
}

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
    modified?: Modified;
}

export interface GetBoxLoadingAction {
    type: GetBoxActionType.LOADING,
    loading: boolean
}

export interface GetBoxSuccessAction {
    type: GetBoxActionType.SUCCESS,
    payload: Box
}

export interface GetBoxErrorAction {
    type: GetBoxActionType.ERROR,
    error: Error
}

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

export interface CreateBoxLoadingAction {
    type: CreateBoxActionType.LOADING,
    loading: boolean
}

export interface CreateBoxSuccessAction {
    type: CreateBoxActionType.SUCCESS,
    payload: Box
}

export interface CreateBoxErrorAction {
    type: CreateBoxActionType.ERROR,
    error: Error
}

export interface UpdateBoxLoadingAction {
    type: UpdateBoxActionType.LOADING,
    loading: boolean
}

export interface UpdateBoxSuccessAction {
    type: UpdateBoxActionType.SUCCESS,
    payload: Box
}

export interface UpdateBoxErrorAction {
    type: UpdateBoxActionType.ERROR,
    error: Error
}

export type GetBoxAction = GetBoxLoadingAction | GetBoxSuccessAction | GetBoxErrorAction;
export type FindBoxesAction = FindBoxesLoadingAction | FindBoxesSuccessAction | FindBoxesErrorAction;
export type CreateBoxAction = CreateBoxLoadingAction | CreateBoxSuccessAction | CreateBoxErrorAction;
export type UpdateBoxAction = UpdateBoxLoadingAction | UpdateBoxSuccessAction | UpdateBoxErrorAction;

export type BoxAction = GetBoxAction | FindBoxesAction | CreateBoxAction | UpdateBoxAction;