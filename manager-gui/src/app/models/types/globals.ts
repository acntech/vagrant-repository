import { GroupState, initialGroupState } from './groups';
import { BoxState, initialBoxState } from './boxes';
import { VersionState, initialVersionState } from './versions';
import { ProviderState, initialProviderState } from './providers';

export interface RootState {
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
    providerState: ProviderState;
}

export const initialRootState: RootState = {
    groupState: initialGroupState,
    boxState: initialBoxState,
    versionState: initialVersionState,
    providerState: initialProviderState
};

export interface Error {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}