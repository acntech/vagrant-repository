import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Container, Header, Icon, Message, Segment, Table } from 'semantic-ui-react';

import { Box, BoxState, Group, GroupState, RootState, Version, VersionState } from '../../models';
import { findBoxVersions, findGroups, findGroupBoxes } from '../../state/actions';
import { MainHeader, LoadingIndicator } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
}

interface ComponentDispatchProps {
    findGroups: (name?: string) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    box?: Box;
    versionId?: number;
    createVersion: boolean;
}

const initialState: ComponentState = {
    createVersion: false
};

class BoxContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId } = this.props.match.params;
        this.props.findGroups();
        this.props.findGroupBoxes(Number(groupId));
        this.props.findBoxVersions(Number(boxId));
    }

    componentDidUpdate() {
        const { groupId, boxId } = this.props.match.params;
        const { group, box } = this.state;

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
    }

    public render(): ReactNode {
        const { groupId, boxId } = this.props.match.params;
        const { versionId, group, box, createVersion } = this.state;
        const { versionState } = this.props;
        const { versions, loading } = versionState;

        if (versionId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createVersion) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/create/version`} />;
        } else if (!group) {
            return <NoGroupFoundFragment groupId={groupId} />;
        } else if (!box) {
            return <NoBoxFoundFragment boxId={boxId} />;
        } else {
            return <BoxFragment
                group={group}
                box={box}
                versions={versions}
                onTableRowClick={this.onTableRowClick}
                onCreateVersionButtonClick={this.onCreateVersionButtonClick} />;
        }
    }

    private onTableRowClick = (versionId: number) => {
        this.setState({ versionId: versionId });
    };

    private onCreateVersionButtonClick = () => {
        this.setState({ createVersion: true });
    };
}

interface BoxFragmentProps {
    group?: Group;
    box?: Box;
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
}

const BoxFragment: SFC<BoxFragmentProps> = (props) => {
    const { group, box, versions, onTableRowClick, onCreateVersionButtonClick } = props;

    if (group && box) {
        const { id: groupId, name: groupName } = group;
        const { id: boxId, name: boxName, description } = box;
        return (
            <Container>
                <MainHeader />
                <Segment basic>
                    <Header>
                        <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link> / <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>
                    </Header>
                    <Header.Subheader>{description}</Header.Subheader>
                </Segment>
                <VersionsFragment
                    versions={versions}
                    onTableRowClick={onTableRowClick}
                    onCreateVersionButtonClick={onCreateVersionButtonClick} />
            </Container>
        );
    } else {
        return (
            <Container>
                <MainHeader headerTitle='Vagrant Repository Manager' />
                <VersionsFragment
                    versions={versions}
                    onTableRowClick={onTableRowClick}
                    onCreateVersionButtonClick={onCreateVersionButtonClick} />
            </Container>
        );
    }
}

interface VersionsFragmentProps {
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
}

const VersionsFragment: SFC<VersionsFragmentProps> = (props) => {
    const { versions, onTableRowClick, onCreateVersionButtonClick } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateVersionButtonClick}>
                    <Icon name='tag' />New Version
                </Button>
            </Button.Group>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Version</Table.HeaderCell>
                        <Table.HeaderCell width={10}>Description</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {versions.map((version, index) => {
                        const { id, name, description } = version;
                        return (
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onTableRowClick(id)}>
                                <Table.Cell>{name}</Table.Cell>
                                <Table.Cell>{description}</Table.Cell>
                            </Table.Row>
                        );
                    })}
                </Table.Body>
            </Table>
        </Segment>
    );
};

interface NoGroupFoundFragmentProps {
    groupId: string;
};

const NoGroupFoundFragment: SFC<NoGroupFoundFragmentProps> = (props) => {
    const { groupId } = props;

    return (
        <Container>
            <MainHeader />
            <Segment basic>
                <Message
                    negative
                    icon='warning sign'
                    header='No group found'
                    content={`Could not find group for ID ${groupId}`} />
            </Segment>
        </Container>
    );
};

interface NoBoxFoundFragmentProps {
    boxId: string;
};

const NoBoxFoundFragment: SFC<NoBoxFoundFragmentProps> = (props) => {
    const { boxId } = props;

    return (
        <Container>
            <MainHeader />
            <Segment basic>
                <Message
                    negative
                    icon='warning sign'
                    header='No box found'
                    content={`Could not find box for ID ${boxId}`} />
            </Segment>
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState,
    versionState: state.versionState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroups: (name?: string) => dispatch(findGroups(name)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId))
});

const ConnectedBoxContainer = connect(mapStateToProps, mapDispatchToProps)(BoxContainer);

export { ConnectedBoxContainer as BoxContainer };