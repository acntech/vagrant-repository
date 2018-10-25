import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Dimmer, Loader, Table } from 'semantic-ui-react';

import { Box, BoxState, RootState } from '../../models';
import { findBoxes } from '../../state/actions';
import { MainHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    boxState: BoxState;
}

interface ComponentDispatchProps {
    findBoxes: (groupId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    boxId?: number;
}

const initialState: ComponentState = {};

class GroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount(){
        this.props.findBoxes(Number(this.props.match.params.groupId));
    }


    public render(): ReactNode {
        const { boxId } = this.state;
        const { boxState } = this.props;
//        const loading = groupState.loading;
        const { boxes, loading } = boxState; 

        if (boxId) {
            return <Redirect to={`/box/${boxId}`} />;
        } else if (loading) {
            return <Loading />;
        } else {
            console.log(boxes)
            return (
                <div>
                    <MainHeader title='Vagrant Repository Manager' />
                    <Boxes boxes={boxes} onClick={this.onListItemClick} />
                </div>
            );
        }
    }
    private onListItemClick = (boxId: number) => {
        this.setState({ boxId: boxId });
    };
}

interface BoxesProps {
    boxes: Box[];
    onClick: (groupId: number) => void;
}

const Boxes: SFC<BoxesProps> = (props) => {
    const { boxes, onClick } = props;
    return (
        <div>
        {boxes[0] ? <h3>Group: {boxes[0].group.name}</h3> : null}
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell>Box name</Table.HeaderCell>
                        <Table.HeaderCell>Description</Table.HeaderCell>
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
        </div>
    );
};

const Loading: SFC<{}> = () => {
    return (
        <div>
            <MainHeader title='Vagrant Repository Manager' />
            <Dimmer inverted active>
                <Loader>Loading</Loader>
            </Dimmer>
        </div>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    boxState: state.boxState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
        findBoxes:(groupId: number) => dispatch(findBoxes(groupId))

});

const ConnectedGroupContainer = connect(mapStateToProps, mapDispatchToProps)(GroupContainer);

export { ConnectedGroupContainer as GroupContainer };