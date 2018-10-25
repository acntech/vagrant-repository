import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Container, Header, Segment, Table } from 'semantic-ui-react';

import { Box, BoxState, Group, GroupState, RootState } from '../../models';
import { findGroupBoxes } from '../../state/actions';
import { MainHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
}

interface ComponentDispatchProps {
    findGroupBoxes: (groupId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    boxId?: number;
}

const initialState: ComponentState = {};

class GroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId } = this.props.match.params;
        this.props.findGroupBoxes(Number(groupId));
    }

    componentDidUpdate() {
        const { groupId } = this.props.match.params;
        const { group } = this.state;
        if (!group) {
            const { groups } = this.props.groupState;
            const currentGroup = groups.find((group) => group.id == groupId);
            if (currentGroup) {
                this.setState({ group: currentGroup });
            }
        }
    }

    public render(): ReactNode {
        const { boxId, group } = this.state;
        const { boxState } = this.props;
        const { boxes, loading } = boxState;

        if (boxId) {
            return <Redirect to={`/box/${boxId}`} />;
        } else if (loading) {
            return <LoadingFragment />;
        } else {
            return (
                <div>
                    <GroupFragment group={group} boxes={boxes} onClick={this.onListItemClick} />

                </div>
            );
        }
    }
    private onListItemClick = (boxId: number) => {
        this.setState({ boxId: boxId });
    };
}

interface GroupFragmentProps {
    group?: Group;
    boxes: Box[];
    onClick: (groupId: number) => void;
}

const GroupFragment: SFC<GroupFragmentProps> = (props) => {
    const { group, boxes, onClick } = props;

    if (group) {
        const { name, description } = group;
        return (
            <Container>
                <MainHeader title='Vagrant Repository Manager' />
                <Segment basic>
                    <Header>{name}</Header>
                    <Header.Subheader>{description}</Header.Subheader>
                </Segment>
                <BoxesFragment boxes={boxes} onClick={onClick} />
            </Container>
        );
    } else {
        return (
            <Container>
                <MainHeader title='Vagrant Repository Manager' />
                <BoxesFragment boxes={boxes} onClick={onClick} />
            </Container>
        );
    }
}

interface BoxesFragmentProps {
    boxes: Box[];
    onClick: (groupId: number) => void;
}

const BoxesFragment: SFC<BoxesFragmentProps> = (props) => {
    const { boxes, onClick } = props;

    return (
        <Segment basic>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Box name</Table.HeaderCell>
                        <Table.HeaderCell width={10}>Description</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {boxes.map((box, index) => {
                        const { id, name, description } = box;
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
            <MainHeader title='Vagrant Repository Manager' />
            <Segment loading />
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId))

});

const ConnectedGroupContainer = connect(mapStateToProps, mapDispatchToProps)(GroupContainer);

export { ConnectedGroupContainer as GroupContainer };