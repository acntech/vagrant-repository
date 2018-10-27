import { addLocaleData } from "react-intl";
import * as enLocaleData from 'react-intl/locale-data/en';
import { IntlState } from 'react-intl-redux';
import { BoxState, GroupState, VersionState, ProviderState, RootState } from '../../models';

export const initialGroupState: GroupState = {
    loading: false,
    groups: []
};

export const initialBoxState: BoxState = {
    loading: false,
    boxes: []
};

export const initialVersionState: VersionState = {
    loading: false,
    versions: []
};

export const initialProviderState: ProviderState = {
    loading: false,
    providers: []
};

const INITIAL_STATE_MESSAGES = {
};

export const intitialIntlState: IntlState = {
    locale: 'en',
    messages: { ...INITIAL_STATE_MESSAGES }
};

addLocaleData([...enLocaleData]);

export const initialRootState: RootState = {
    intl: intitialIntlState,
    groupState: initialGroupState,
    boxState: initialBoxState,
    versionState: initialVersionState,
    providerState: initialProviderState
};