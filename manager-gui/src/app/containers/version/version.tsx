import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Container, Icon, Segment, Table } from 'semantic-ui-react';

import { formatBytes } from '../../core/utils';
import { Box, BoxState, Group, GroupState, Provider, ProviderState, RootState, Version, VersionState } from '../../models';
import { findBoxVersions, findGroups, findGroupBoxes, findVersionProviders } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '../';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    findGroups: (name?: string) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
    findVersionProviders: (versionId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    box?: Box;
    version?: Version;
    providerId?: number;
    createProvider: boolean;
}

const initialState: ComponentState = {
    createProvider: false
};

class VersionContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId, versionId } = this.props.match.params;
        this.props.findGroups();
        this.props.findGroupBoxes(Number(groupId));
        this.props.findBoxVersions(Number(boxId));
        this.props.findVersionProviders(Number(versionId));
    }

    componentDidUpdate() {
        const { groupId, boxId, versionId } = this.props.match.params;
        const { group, box, version } = this.state;

        if (!group) {
            const { groups } = this.props.groupState;
            const currentGroup = groups.find((group) => group.id == groupId);
            if (currentGroup) {
                this.setState({ group: currentGroup });
            }
        }

        if (!box) {
            const { boxes } = this.props.boxState;
            const currentBox = boxes.find((box) => box.id == boxId);
            if (currentBox) {
                this.setState({ box: currentBox });
            }
        }

        if (!version) {
            const { versions } = this.props.versionState;
            const currentVersion = versions.find((version) => version.id == versionId);
            if (currentVersion) {
                this.setState({ version: currentVersion });
            }
        }
    }

    public render(): ReactNode {
        const { groupId, boxId, versionId } = this.props.match.params;
        const { createProvider, group, box, providerId, version } = this.state;
        const { providerState } = this.props;
        const { providers, loading } = providerState;

        if (providerId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createProvider) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/create`} />;
        } else if (!group) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No group found'
                content={`Could not find group for ID ${groupId}`} />;
        } else if (!box) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No box found'
                content={`Could not find box for ID ${boxId}`} />;
        } else if (!version) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No version found'
                content={`Could not find version for ID ${versionId}`} />;
        } else {
            const versionProviders = providers.filter(provider => provider.version.id == versionId);

            return <VersionFragment
                group={group}
                box={box}
                version={version}
                providers={versionProviders}
                onTableRowClick={this.onTableRowClick}
                onCreateProviderButtonClick={this.onCreateProviderButtonClick} />;
        }
    }

    private onTableRowClick = (providerId: number) => {
        this.setState({ providerId: providerId });
    };

    private onCreateProviderButtonClick = () => {
        this.setState({ createProvider: true });
    };
}

interface VersionFragmentProps {
    group: Group;
    box: Box;
    version: Version;
    providers: Provider[];
    onTableRowClick: (providerId: number) => void;
    onCreateProviderButtonClick: () => void;
}

const VersionFragment: SFC<VersionFragmentProps> = (props) => {
    const { group, box, version, providers, onTableRowClick, onCreateProviderButtonClick } = props;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName } = box;
    const { id: versionId, name: versionName, description } = version;

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader subtitle={description}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>
            </SecondaryHeader>
            <ProvidersFragment
                providers={providers}
                onTableRowClick={onTableRowClick}
                onCreateProviderButtonClick={onCreateProviderButtonClick} />
        </Container>
    );
}

interface ProvidersFragmentProps {
    providers: Provider[];
    onTableRowClick: (providerId: number) => void;
    onCreateProviderButtonClick: () => void;
}

const ProvidersFragment: SFC<ProvidersFragmentProps> = (props) => {
    const { providers, onTableRowClick, onCreateProviderButtonClick } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateProviderButtonClick}>
                    <Icon name='file' />New Provider
                </Button>
            </Button.Group>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Provider</Table.HeaderCell>
                        <Table.HeaderCell width={4}>Size</Table.HeaderCell>
                        <Table.HeaderCell width={4}>Checksum type</Table.HeaderCell>
                        <Table.HeaderCell width={4}>Checksum</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {providers.map((provider, index) => {
                        const { id, providerType, size, checksumType, checksum } = provider;
                        return (
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onTableRowClick(id)}>
                                <Table.Cell>{providerType}</Table.Cell>
                                <Table.Cell>{formatBytes(size, 0)}</Table.Cell>
                                <Table.Cell>{checksumType}</Table.Cell>
                                <Table.Cell>{checksum}</Table.Cell>
                            </Table.Row>
                        );
                    })}
                </Table.Body>
            </Table>
        </Segment>
    );
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState,
    versionState: state.versionState,
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroups: (name?: string) => dispatch(findGroups(name)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId)),
    findVersionProviders: (versionId: number) => dispatch(findVersionProviders(versionId))
});

const ConnectedVersionContainer = connect(mapStateToProps, mapDispatchToProps)(VersionContainer);

export { ConnectedVersionContainer as VersionContainer };