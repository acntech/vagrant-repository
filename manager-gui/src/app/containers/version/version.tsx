import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Container, Header, Segment, Table } from 'semantic-ui-react';

import { Box, BoxState, Group, GroupState, Provider, ProviderState, RootState, Version, VersionState } from '../../models';
import { findBoxVersions, findGroups, findGroupBoxes, findVersionProviders } from '../../state/actions';
import { MainHeader, LoadingIndicator } from '../../components';

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
}

const initialState: ComponentState = {};

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
        const { groupId, boxId } = this.props.match.params;
        const { providerId, group, box, version } = this.state;
        const { providerState } = this.props;
        const { providers, loading } = providerState;

        if (providerId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${providerId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else {
            return <VersionFragment group={group} box={box} version={version} providers={providers} onClick={this.onListItemClick} />;
        }
    }

    private onListItemClick = (versionId: number) => {
    };
}

interface VersionFragmentProps {
    group?: Group;
    box?: Box;
    version?: Version;
    providers: Provider[];
    onClick: (providerId: number) => void;
}

const VersionFragment: SFC<VersionFragmentProps> = (props) => {
    const { group, box, version, providers, onClick } = props;

    if (group && box && version) {
        const { id: groupId, name: groupName } = group;
        const { id: boxId, name: boxName } = box;
        const { id: versionId, name: versionName, description } = version;
        return (
            <Container>
                <MainHeader />
                <Segment basic>
                    <Header>
                        <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link> / <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link> / <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>
                    </Header>
                    <Header.Subheader>{description}</Header.Subheader>
                </Segment>
                <ProvidersFragment providers={providers} onClick={onClick} />
            </Container>
        );
    } else {
        return (
            <Container>
                <MainHeader />
                <ProvidersFragment providers={providers} onClick={onClick} />
            </Container>
        );
    }
}

interface ProvidersFragmentProps {
    providers: Provider[];
    onClick: (providerId: number) => void;
}

const ProvidersFragment: SFC<ProvidersFragmentProps> = (props) => {
    const { providers, onClick } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny'>New Provider</Button>
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
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onClick(id)}>
                                <Table.Cell>{providerType}</Table.Cell>
                                <Table.Cell>{size}</Table.Cell>
                                <Table.Cell>{checksumType}</Table.Cell>
                                <Table.Cell>{checksum}</Table.Cell>
                            </Table.Row>
                        );
                    })}
                </Table.Body>
            </Table>
        </Segment>
    );
};

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