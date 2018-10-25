import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Container, Header, Segment, Table } from 'semantic-ui-react';

import { Box, BoxState, Group, GroupState, RootState, Version, VersionState } from '../../models';
import { findBoxVersions, findGroups, findGroupBoxes } from '../../state/actions';
import { MainHeader } from '../../components';

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
}

const initialState: ComponentState = {};

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
        const { versionId, group, box } = this.state;
        const { versionState } = this.props;
        const { versions, loading } = versionState;

        if (versionId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingFragment />;
        } else {
            return <BoxFragment group={group} box={box} versions={versions} onClick={this.onListItemClick} />;
        }
    }

    private onListItemClick = (versionId: number) => {
        this.setState({ versionId: versionId });
    };
}

interface BoxFragmentProps {
    group?: Group;
    box?: Box;
    versions: Version[];
    onClick: (versionId: number) => void;
}

const BoxFragment: SFC<BoxFragmentProps> = (props) => {
    const { group, box, versions, onClick } = props;

    if (group && box) {
        const { name: groupName } = group;
        const { name: boxName, description } = box;
        return (
            <Container>
                <MainHeader headerTitle='Vagrant Repository Manager' />
                <Segment basic>
                    <Header>{groupName} / {boxName}</Header>
                    <Header.Subheader>{description}</Header.Subheader>
                </Segment>
                <VersionsFragment versions={versions} onClick={onClick} />
            </Container>
        );
    } else {
        return (
            <Container>
                <MainHeader headerTitle='Vagrant Repository Manager' />
                <VersionsFragment versions={versions} onClick={onClick} />
            </Container>
        );
    }
}

interface VersionsFragmentProps {
    versions: Version[];
    onClick: (versionId: number) => void;
}

const VersionsFragment: SFC<VersionsFragmentProps> = (props) => {
    const { versions, onClick } = props;

    return (
        <Segment basic>
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
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onClick(id)}>
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

const LoadingFragment: SFC<{}> = () => {
    return (
        <Container>
            <MainHeader headerTitle='Vagrant Repository Manager' />
            <Segment loading />
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