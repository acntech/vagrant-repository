import {
    ChecksumType,
    CreateProviderActionType,
    DeleteProviderActionType,
    GetProviderActionType,
    FindProvidersActionType,
    ProviderType,
    UpdateProviderActionType,
    Error,
    Modified,
    Version
} from '..';

export interface ModifyProvider {
    providerType: ProviderType;
}

export interface Provider {
    id: number;
    providerType: ProviderType;
    fileName: string;
    size: number;
    checksumType: ChecksumType;
    checksum: string;
    version: Version;
}

export interface ProviderState {
    loading: boolean;
    providers: Provider[];
    error?: Error;
    modified?: Modified;
}

export interface GetProviderLoadingAction {
    type: GetProviderActionType.LOADING,
    loading: boolean
}

export interface GetProviderSuccessAction {
    type: GetProviderActionType.SUCCESS,
    payload: Provider
}

export interface GetProviderErrorAction {
    type: GetProviderActionType.ERROR,
    error: any
}

export interface FindProvidersLoadingAction {
    type: FindProvidersActionType.LOADING,
    loading: boolean
}

export interface FindProvidersSuccessAction {
    type: FindProvidersActionType.SUCCESS,
    payload: Provider[]
}

export interface FindProvidersErrorAction {
    type: FindProvidersActionType.ERROR,
    error: any
}

export interface CreateProviderLoadingAction {
    type: CreateProviderActionType.LOADING,
    loading: boolean
}

export interface CreateProviderSuccessAction {
    type: CreateProviderActionType.SUCCESS,
    payload: Provider
}

export interface CreateProviderErrorAction {
    type: CreateProviderActionType.ERROR,
    error: any
}

export interface UpdateProviderLoadingAction {
    type: UpdateProviderActionType.LOADING,
    loading: boolean
}

export interface UpdateProviderSuccessAction {
    type: UpdateProviderActionType.SUCCESS,
    payload: Provider
}

export interface UpdateProviderErrorAction {
    type: UpdateProviderActionType.ERROR,
    error: any
}

export interface DeleteProviderLoadingAction {
    type: DeleteProviderActionType.LOADING,
    loading: boolean
}

export interface DeleteProviderSuccessAction {
    type: DeleteProviderActionType.SUCCESS,
    providerId: number
}

export interface DeleteProviderErrorAction {
    type: DeleteProviderActionType.ERROR,
    error: any
}

export type GetProviderAction = GetProviderLoadingAction | GetProviderSuccessAction | GetProviderErrorAction;
export type FindProvidersAction = FindProvidersLoadingAction | FindProvidersSuccessAction | FindProvidersErrorAction;
export type CreateProviderAction = CreateProviderLoadingAction | CreateProviderSuccessAction | CreateProviderErrorAction;
export type UpdateProviderAction = UpdateProviderLoadingAction | UpdateProviderSuccessAction | UpdateProviderErrorAction;
export type DeleteProviderAction = DeleteProviderLoadingAction | DeleteProviderSuccessAction | DeleteProviderErrorAction;

export type ProviderAction = GetProviderAction | FindProvidersAction | CreateProviderAction | UpdateProviderAction | DeleteProviderAction;