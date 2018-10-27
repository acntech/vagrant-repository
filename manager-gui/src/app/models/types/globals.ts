import { IntlState } from 'react-intl-redux';
import { BoxState, GroupState, ProviderState, VersionState } from './';

export interface RootState {
    intl: IntlState;
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
    providerState: ProviderState;
}

export interface Error {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}